package com.dailystar.service.impl;

import com.dailystar.dao.AccountSnapshotDao;
import com.dailystar.dao.SyncProfileStateDao;
import com.dailystar.dao.SyncRecordDao;
import com.dailystar.dto.SyncDeltaChangeRequest;
import com.dailystar.dto.SyncDeltaConflictPayload;
import com.dailystar.dto.SyncDeltaRequest;
import com.dailystar.dto.SyncDeltaResponse;
import com.dailystar.dto.SyncConflictPayload;
import com.dailystar.dto.SyncProfileRequest;
import com.dailystar.dto.SyncProfileResponse;
import com.dailystar.dto.SyncRemoteChangeResponse;
import com.dailystar.entity.AccountSnapshotEntity;
import com.dailystar.entity.SyncProfileStateEntity;
import com.dailystar.entity.SyncRecordEntity;
import com.dailystar.enums.MessageCodeEnum;
import com.dailystar.enums.SyncResolveStrategy;
import com.dailystar.exception.BusinessException;
import com.dailystar.service.SyncService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class SyncServiceImpl implements SyncService {

    private static final String ACTION_NO_CHANGE = "NO_CHANGE";
    private static final String ACTION_UPLOAD = "UPLOAD";
    private static final String ACTION_DOWNLOAD = "DOWNLOAD";
    private static final String ACTION_CONFLICT = "CONFLICT";
    private static final String ACTION_SYNCED = "SYNCED";
    private static final long INITIAL_CURSOR = 0L;

    private final AccountSnapshotDao accountSnapshotDao;
    private final SyncProfileStateDao syncProfileStateDao;
    private final SyncRecordDao syncRecordDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SyncDeltaResponse syncDelta(Long accountId, SyncDeltaRequest request) {
        if (accountId == null) {
            throw new BusinessException(MessageCodeEnum.UNAUTHORIZED);
        }
        String resolvedProfileId = defaultProfileId(request.getProfileId(), "cloud_" + accountId);
        SyncResolveStrategy resolveStrategy = SyncResolveStrategy.from(request.getResolveStrategy());
        SyncProfileStateEntity state = getOrCreateProfileState(accountId, resolvedProfileId);
        if (!StringUtils.hasText(state.getProfileId())) {
            state.setProfileId(resolvedProfileId);
        }
        List<SyncDeltaChangeRequest> changes = request.getChanges() == null ? Collections.emptyList() : request.getChanges();
        List<SyncDeltaChangeRequest> conflicts = findConflictingChanges(accountId, changes);
        if (resolveStrategy == SyncResolveStrategy.NORMAL && !conflicts.isEmpty()) {
            return buildConflictResponse(state, conflicts);
        }
        if (resolveStrategy != SyncResolveStrategy.FORCE_DOWNLOAD) {
            applyChanges(accountId, state, changes);
        }
        List<SyncRecordEntity> remoteChanges = syncRecordDao.selectByAccountAndVersionAfter(accountId, defaultCursor(request.getLastCursor()));
        syncProfileStateDao.updateById(state);
        return SyncDeltaResponse.builder()
            .action(remoteChanges.isEmpty() && changes.isEmpty() ? ACTION_NO_CHANGE : ACTION_SYNCED)
            .profileId(state.getProfileId())
            .nextCursor(state.getCurrentCursor())
            .message(remoteChanges.isEmpty() && changes.isEmpty() ? "当前没有新的同步内容" : "增量同步完成")
            .remoteChanges(toRemoteChanges(remoteChanges))
            .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SyncProfileResponse syncProfile(Long accountId, SyncProfileRequest request) {
        if (accountId == null) {
            throw new BusinessException(MessageCodeEnum.UNAUTHORIZED);
        }
        AccountSnapshotEntity remote = accountSnapshotDao.selectByAccountId(accountId).orElse(null);
        boolean hasLocalSnapshot = StringUtils.hasText(request.getSnapshotJson()) && request.getSnapshotUpdatedAt() != null;
        SyncResolveStrategy resolveStrategy = SyncResolveStrategy.from(request.getResolveStrategy());

        if (remote == null && !hasLocalSnapshot) {
            return SyncProfileResponse.builder()
                .action(ACTION_NO_CHANGE)
                .message("当前没有可同步的数据")
                .build();
        }

        if (remote == null) {
            AccountSnapshotEntity created = createSnapshot(accountId, request);
            accountSnapshotDao.insertSelective(created);
            return buildResponse(ACTION_UPLOAD, created, "本地数据已上传到云端");
        }

        if (!hasLocalSnapshot) {
            return buildResponse(ACTION_DOWNLOAD, remote, "已拉取云端数据");
        }

        if (sameSnapshot(remote, request)) {
            return buildResponse(ACTION_NO_CHANGE, remote, "本地与云端已经是最新一致状态");
        }

        if (resolveStrategy == SyncResolveStrategy.FORCE_DOWNLOAD) {
            return buildResponse(ACTION_DOWNLOAD, remote, "已使用云端档案覆盖本地");
        }

        if (resolveStrategy == SyncResolveStrategy.FORCE_UPLOAD) {
            AccountSnapshotEntity overwritten = overwriteRemote(remote, request);
            return buildResponse(ACTION_UPLOAD, overwritten, "已使用本地档案覆盖云端");
        }

        if (request.getBaseRevision() != null && request.getBaseRevision().equals(remote.getSyncRevision())) {
            AccountSnapshotEntity updated = overwriteRemote(remote, request);
            return buildResponse(ACTION_UPLOAD, updated, "本地数据已同步到云端");
        }

        if (localStillMatchesBase(request)) {
            return buildResponse(ACTION_DOWNLOAD, remote, "检测到云端有新版本，已返回云端最新档案");
        }

        return SyncProfileResponse.builder()
            .action(ACTION_CONFLICT)
            .profileId(remote.getProfileId())
            .snapshotJson(remote.getSnapshotJson())
            .snapshotUpdatedAt(remote.getSnapshotUpdatedAt())
            .snapshotHash(remote.getSnapshotHash())
            .syncRevision(remote.getSyncRevision())
            .message("检测到云端和本地都发生了变化，请选择保留哪一份数据")
            .conflict(
                SyncConflictPayload.builder()
                    .profileId(remote.getProfileId())
                    .remoteRevision(remote.getSyncRevision())
                    .remoteUpdatedAt(remote.getSnapshotUpdatedAt())
                    .remoteSnapshotHash(remote.getSnapshotHash())
                    .localUpdatedAt(request.getSnapshotUpdatedAt())
                    .localSnapshotHash(request.getSnapshotHash())
                    .message("云端和本地都发生了新修改")
                    .build()
            )
            .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearProfile(Long accountId) {
        if (accountId == null) {
            throw new BusinessException(MessageCodeEnum.UNAUTHORIZED);
        }
        accountSnapshotDao.deleteByAccountId(accountId);
        syncRecordDao.deleteByAccountId(accountId);
        syncProfileStateDao.deleteByAccountId(accountId);
    }

    private SyncProfileStateEntity getOrCreateProfileState(Long accountId, String profileIdValue) {
        Optional<SyncProfileStateEntity> existing = syncProfileStateDao.selectByAccountId(accountId);
        if (existing.isPresent()) {
            return existing.get();
        }
        LocalDateTime now = LocalDateTime.now();
        SyncProfileStateEntity created = SyncProfileStateEntity.builder()
            .accountId(accountId)
            .profileId(profileIdValue)
            .currentCursor(INITIAL_CURSOR)
            .createdAt(now)
            .updatedAt(now)
            .build();
        syncProfileStateDao.insertSelective(created);
        return created;
    }

    private List<SyncDeltaChangeRequest> findConflictingChanges(Long accountId, List<SyncDeltaChangeRequest> changes) {
        List<SyncDeltaChangeRequest> conflicts = new ArrayList<>();
        for (SyncDeltaChangeRequest change : changes) {
            SyncRecordEntity remote = syncRecordDao.selectByAccountAndEntity(accountId, change.getEntityType(), change.getSyncId()).orElse(null);
            long baseVersion = defaultCursor(change.getBaseVersion());
            long remoteVersion = remote == null ? INITIAL_CURSOR : defaultCursor(remote.getServerVersion());
            if (sameState(remote, change)) {
                continue;
            }
            if (remote == null && baseVersion == INITIAL_CURSOR) {
                continue;
            }
            if (remoteVersion != baseVersion) {
                conflicts.add(change);
            }
        }
        return conflicts;
    }

    private void applyChanges(Long accountId, SyncProfileStateEntity state, List<SyncDeltaChangeRequest> changes) {
        for (SyncDeltaChangeRequest change : changes) {
            SyncRecordEntity remote = syncRecordDao.selectByAccountAndEntity(accountId, change.getEntityType(), change.getSyncId()).orElse(null);
            if (sameState(remote, change)) {
                continue;
            }
            long nextCursor = defaultCursor(state.getCurrentCursor()) + 1L;
            state.setCurrentCursor(nextCursor);
            state.setUpdatedAt(LocalDateTime.now());
            if (remote == null) {
                SyncRecordEntity created = SyncRecordEntity.builder()
                    .accountId(accountId)
                    .profileId(state.getProfileId())
                    .entityType(change.getEntityType())
                    .entitySyncId(change.getSyncId())
                    .operationType(change.getOperation())
                    .payloadJson(change.getPayloadJson())
                    .payloadHash(change.getPayloadHash())
                    .serverVersion(nextCursor)
                    .clientUpdatedAt(defaultCursor(change.getClientUpdatedAt()))
                    .updatedAt(LocalDateTime.now())
                    .build();
                syncRecordDao.insertSelective(created);
            } else {
                remote.setProfileId(state.getProfileId());
                remote.setOperationType(change.getOperation());
                remote.setPayloadJson(change.getPayloadJson());
                remote.setPayloadHash(change.getPayloadHash());
                remote.setServerVersion(nextCursor);
                remote.setClientUpdatedAt(defaultCursor(change.getClientUpdatedAt()));
                remote.setUpdatedAt(LocalDateTime.now());
                syncRecordDao.updateById(remote);
            }
        }
    }

    private SyncDeltaResponse buildConflictResponse(SyncProfileStateEntity state, List<SyncDeltaChangeRequest> conflicts) {
        LinkedHashSet<String> entityTypes = new LinkedHashSet<>();
        for (SyncDeltaChangeRequest conflict : conflicts) {
            entityTypes.add(conflict.getEntityType());
        }
        return SyncDeltaResponse.builder()
            .action(ACTION_CONFLICT)
            .profileId(state.getProfileId())
            .nextCursor(state.getCurrentCursor())
            .message("检测到云端与本地存在冲突，请选择保留云端还是本地数据")
            .conflict(
                SyncDeltaConflictPayload.builder()
                    .message("检测到云端与本地存在冲突，请选择保留云端还是本地数据")
                    .conflictCount(conflicts.size())
                    .entityTypes(new ArrayList<>(entityTypes))
                    .build()
            )
            .build();
    }

    private List<SyncRemoteChangeResponse> toRemoteChanges(List<SyncRecordEntity> records) {
        List<SyncRemoteChangeResponse> responses = new ArrayList<>();
        for (SyncRecordEntity record : records) {
            responses.add(
                SyncRemoteChangeResponse.builder()
                    .entityType(record.getEntityType())
                    .syncId(record.getEntitySyncId())
                    .operation(record.getOperationType())
                    .payloadJson(record.getPayloadJson())
                    .payloadHash(record.getPayloadHash())
                    .serverVersion(record.getServerVersion())
                    .clientUpdatedAt(record.getClientUpdatedAt())
                    .build()
            );
        }
        return responses;
    }

    private boolean sameState(SyncRecordEntity remote, SyncDeltaChangeRequest change) {
        if (remote == null) {
            return false;
        }
        if (!stringEquals(remote.getOperationType(), change.getOperation())) {
            return false;
        }
        if ("DELETE".equalsIgnoreCase(change.getOperation())) {
            return true;
        }
        return stringEquals(remote.getPayloadHash(), change.getPayloadHash());
    }

    private boolean stringEquals(String left, String right) {
        if (left == null) {
            return right == null;
        }
        return left.equals(right);
    }

    private long defaultCursor(Long value) {
        return value == null ? INITIAL_CURSOR : value;
    }

    private AccountSnapshotEntity createSnapshot(Long accountId, SyncProfileRequest request) {
        LocalDateTime now = LocalDateTime.now();
        return AccountSnapshotEntity.builder()
            .accountId(accountId)
            .profileId(defaultProfileId(request.getProfileId(), "cloud_" + accountId))
            .snapshotJson(request.getSnapshotJson())
            .snapshotUpdatedAt(request.getSnapshotUpdatedAt())
            .snapshotHash(request.getSnapshotHash())
            .syncRevision(1L)
            .lastDeviceId(request.getDeviceId())
            .createdAt(now)
            .updatedAt(now)
            .build();
    }

    private AccountSnapshotEntity overwriteRemote(AccountSnapshotEntity remote, SyncProfileRequest request) {
        remote.setProfileId(defaultProfileId(request.getProfileId(), remote.getProfileId()));
        remote.setSnapshotJson(request.getSnapshotJson());
        remote.setSnapshotUpdatedAt(request.getSnapshotUpdatedAt());
        remote.setSnapshotHash(request.getSnapshotHash());
        remote.setSyncRevision((remote.getSyncRevision() == null ? 0L : remote.getSyncRevision()) + 1L);
        remote.setLastDeviceId(request.getDeviceId());
        remote.setUpdatedAt(LocalDateTime.now());
        accountSnapshotDao.updateById(remote);
        return remote;
    }

    private SyncProfileResponse buildResponse(String action, AccountSnapshotEntity snapshot, String message) {
        return SyncProfileResponse.builder()
            .action(action)
            .profileId(snapshot.getProfileId())
            .snapshotJson(snapshot.getSnapshotJson())
            .snapshotUpdatedAt(snapshot.getSnapshotUpdatedAt())
            .snapshotHash(snapshot.getSnapshotHash())
            .syncRevision(snapshot.getSyncRevision())
            .message(message)
            .build();
    }

    private String defaultProfileId(String preferredProfileId, String fallback) {
        return StringUtils.hasText(preferredProfileId) ? preferredProfileId : fallback;
    }

    private boolean sameSnapshot(AccountSnapshotEntity remote, SyncProfileRequest request) {
        return StringUtils.hasText(request.getSnapshotHash())
            && request.getSnapshotHash().equals(remote.getSnapshotHash());
    }

    private boolean localStillMatchesBase(SyncProfileRequest request) {
        return StringUtils.hasText(request.getSnapshotHash())
            && request.getSnapshotHash().equals(request.getBaseSnapshotHash());
    }
}

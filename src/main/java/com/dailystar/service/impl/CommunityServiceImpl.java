package com.dailystar.service.impl;

import com.dailystar.dao.AccountFollowDao;
import com.dailystar.dao.AccountProfileDao;
import com.dailystar.dao.CommunityPostDao;
import com.dailystar.dao.CommunityPostLikeDao;
import com.dailystar.dto.CommunityFeedResponse;
import com.dailystar.dto.CommunityFollowActionResponse;
import com.dailystar.dto.CommunityLikeActionResponse;
import com.dailystar.dto.CommunityPostResponse;
import com.dailystar.dto.CommunityShareRequest;
import com.dailystar.dto.CommunityUserPageResponse;
import com.dailystar.dto.CommunityUserProfileResponse;
import com.dailystar.entity.AccountFollowEntity;
import com.dailystar.entity.AccountProfileEntity;
import com.dailystar.entity.CommunityPostEntity;
import com.dailystar.entity.CommunityPostLikeEntity;
import com.dailystar.enums.MessageCodeEnum;
import com.dailystar.exception.BusinessException;
import com.dailystar.model.AccountMetricCountModel;
import com.dailystar.model.PostMetricCountModel;
import com.dailystar.service.CommunityService;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class CommunityServiceImpl implements CommunityService {

    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 50;

    private final AccountProfileDao accountProfileDao;
    private final CommunityPostDao communityPostDao;
    private final CommunityPostLikeDao communityPostLikeDao;
    private final AccountFollowDao accountFollowDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommunityPostResponse share(Long currentAccountId, CommunityShareRequest request) {
        AccountProfileEntity profile = requireCompletedProfile(currentAccountId);
        LocalDateTime now = LocalDateTime.now();
        CommunityPostEntity post = communityPostDao
            .selectLatestByAccountAndDateAndGoal(currentAccountId, request.getSharedDate().trim(), request.getGoalTitle().trim())
            .orElseGet(() -> CommunityPostEntity.builder()
                .accountId(currentAccountId)
                .createdAt(now)
                .build());
        post.setNicknameSnapshot(profile.getNickname().trim());
        post.setAvatarSnapshot(profile.getAvatar().trim());
        post.setGenderSnapshot(profile.getGender());
        post.setSharedDate(request.getSharedDate().trim());
        post.setGoalTitle(request.getGoalTitle().trim());
        post.setGoalCategory(request.getGoalCategory().trim());
        post.setCompletionStatus(request.getCompletionStatus().trim());
        post.setRewardText(trimToNull(request.getRewardText()));
        post.setUpdatedAt(now);
        if (post.getId() == null) {
            communityPostDao.insertSelective(post);
        } else {
            communityPostDao.updateById(post);
        }
        return toPostResponse(post, profile, false, 0L, false);
    }

    @Override
    public CommunityFeedResponse feed(Long currentAccountId, Integer page, Integer pageSize) {
        int safePage = page == null || page.intValue() < 1 ? DEFAULT_PAGE : page.intValue();
        int safePageSize = pageSize == null || pageSize.intValue() < 1 ? DEFAULT_PAGE_SIZE : Math.min(pageSize.intValue(), MAX_PAGE_SIZE);
        long offset = (long) (safePage - 1) * safePageSize;
        List<CommunityPostEntity> rawList = communityPostDao.selectFeedPage(offset, safePageSize + 1L);
        boolean hasMore = rawList.size() > safePageSize;
        List<CommunityPostEntity> pageList = hasMore ? rawList.subList(0, safePageSize) : rawList;
        List<Long> postIds = extractPostIds(pageList);
        Set<Long> followedIds = new HashSet<Long>(accountFollowDao.selectFolloweeIdsByFollower(currentAccountId));
        Map<Long, AccountProfileEntity> profileMap = buildProfileMap(pageList);
        Map<Long, Long> likeCountMap = buildPostCountMap(communityPostLikeDao.countByPostIds(postIds));
        Set<Long> likedPostIds = buildLikedPostSet(currentAccountId, postIds);
        List<CommunityPostResponse> items = new ArrayList<CommunityPostResponse>();
        for (CommunityPostEntity entity : pageList) {
            boolean followed = entity.getAccountId() != null
                && !entity.getAccountId().equals(currentAccountId)
                && followedIds.contains(entity.getAccountId());
            AccountProfileEntity latestProfile = entity.getAccountId() == null ? null : profileMap.get(entity.getAccountId());
            items.add(toPostResponse(
                entity,
                latestProfile,
                followed,
                readPostCount(likeCountMap, entity.getId()),
                likedPostIds.contains(entity.getId())
            ));
        }
        return CommunityFeedResponse.builder()
            .page(safePage)
            .pageSize(safePageSize)
            .hasMore(hasMore)
            .items(items)
            .build();
    }

    @Override
    public CommunityUserProfileResponse userProfile(Long currentAccountId, Long targetAccountId) {
        AccountProfileEntity profile = requireTargetProfile(targetAccountId);
        return buildUserProfileResponse(currentAccountId, targetAccountId, profile);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommunityFollowActionResponse follow(Long currentAccountId, Long targetAccountId) {
        if (currentAccountId.equals(targetAccountId)) {
            throw new BusinessException(MessageCodeEnum.FOLLOW_SELF_NOT_ALLOWED);
        }
        requireTargetProfile(targetAccountId);
        accountFollowDao.selectByPair(currentAccountId, targetAccountId).orElseGet(() -> {
            AccountFollowEntity follow = AccountFollowEntity.builder()
                .followerAccountId(currentAccountId)
                .followeeAccountId(targetAccountId)
                .createdAt(LocalDateTime.now())
                .build();
            accountFollowDao.insertSelective(follow);
            return follow;
        });
        return CommunityFollowActionResponse.builder()
            .accountId(targetAccountId)
            .followedByCurrentUser(true)
            .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommunityFollowActionResponse unfollow(Long currentAccountId, Long targetAccountId) {
        if (currentAccountId.equals(targetAccountId)) {
            throw new BusinessException(MessageCodeEnum.FOLLOW_SELF_NOT_ALLOWED);
        }
        requireTargetProfile(targetAccountId);
        accountFollowDao.deleteByPair(currentAccountId, targetAccountId);
        return CommunityFollowActionResponse.builder()
            .accountId(targetAccountId)
            .followedByCurrentUser(false)
            .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommunityLikeActionResponse likePost(Long currentAccountId, Long postIdValue) {
        CommunityPostEntity post = communityPostDao.selectById(postIdValue)
            .orElseThrow(() -> new BusinessException(MessageCodeEnum.COMMUNITY_POST_NOT_FOUND));
        communityPostLikeDao.selectByPostAndAccount(postIdValue, currentAccountId).orElseGet(() -> {
            CommunityPostLikeEntity like = CommunityPostLikeEntity.builder()
                .postId(postIdValue)
                .accountId(currentAccountId)
                .createdAt(LocalDateTime.now())
                .build();
            communityPostLikeDao.insertSelective(like);
            return like;
        });
        return CommunityLikeActionResponse.builder()
            .postId(postIdValue)
            .accountId(post.getAccountId())
            .likedByCurrentUser(true)
            .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommunityLikeActionResponse unlikePost(Long currentAccountId, Long postIdValue) {
        CommunityPostEntity post = communityPostDao.selectById(postIdValue)
            .orElseThrow(() -> new BusinessException(MessageCodeEnum.COMMUNITY_POST_NOT_FOUND));
        communityPostLikeDao.deleteByPostAndAccount(postIdValue, currentAccountId);
        return CommunityLikeActionResponse.builder()
            .postId(postIdValue)
            .accountId(post.getAccountId())
            .likedByCurrentUser(false)
            .build();
    }

    @Override
    public CommunityUserPageResponse followers(Long currentAccountId, Long targetAccountId, Integer page, Integer pageSize) {
        int safePage = normalizePage(page);
        int safePageSize = normalizePageSize(pageSize);
        long offset = (long) (safePage - 1) * safePageSize;
        List<AccountFollowEntity> rawList = accountFollowDao.selectFollowersPage(targetAccountId, offset, safePageSize + 1);
        boolean hasMore = rawList.size() > safePageSize;
        List<AccountFollowEntity> pageList = hasMore ? rawList.subList(0, safePageSize) : rawList;
        List<Long> accountIds = extractAccountIds(pageList, true);
        Map<Long, AccountProfileEntity> profileMap = buildProfileMapByAccountIds(accountIds);
//        Map<Long, Long> followerCountMap = buildCountMap(accountFollowDao.countFollowersByAccountIds(accountIds));
//        Map<Long, Long> followingCountMap = buildCountMap(accountFollowDao.countFollowingByAccountIds(accountIds));
//        Map<Long, Long> shareCountMap = buildCountMap(communityPostDao.countByAccountIds(accountIds));
        Set<Long> followedIds = buildFollowedSet(currentAccountId, accountIds);
        List<CommunityUserProfileResponse> items = new ArrayList<CommunityUserProfileResponse>();
        for (AccountFollowEntity follow : pageList) {
            AccountProfileEntity profile = profileMap.getOrDefault(
                follow.getFollowerAccountId(),
                AccountProfileEntity.builder().accountId(follow.getFollowerAccountId()).build()
            );
            items.add(buildUserProfileResponse(
                currentAccountId,
                follow.getFollowerAccountId(),
                profile,
                followedIds
            ));
        }
        return CommunityUserPageResponse.builder()
            .page(safePage)
            .pageSize(safePageSize)
            .hasMore(hasMore)
            .items(items)
            .build();
    }

    @Override
    public CommunityUserPageResponse following(Long currentAccountId, Long targetAccountId, Integer page, Integer pageSize) {
        int safePage = normalizePage(page);
        int safePageSize = normalizePageSize(pageSize);
        long offset = (long) (safePage - 1) * safePageSize;
        List<AccountFollowEntity> rawList = accountFollowDao.selectFollowingPage(targetAccountId, offset, safePageSize + 1);
        boolean hasMore = rawList.size() > safePageSize;
        List<AccountFollowEntity> pageList = hasMore ? rawList.subList(0, safePageSize) : rawList;
        List<Long> accountIds = extractAccountIds(pageList, false);
        Map<Long, AccountProfileEntity> profileMap = buildProfileMapByAccountIds(accountIds);
        Set<Long> followedIds = buildFollowedSet(currentAccountId, accountIds);
        List<CommunityUserProfileResponse> items = new ArrayList<>();
        for (AccountFollowEntity follow : pageList) {
            AccountProfileEntity profile = profileMap.getOrDefault(
                follow.getFolloweeAccountId(),
                AccountProfileEntity.builder().accountId(follow.getFolloweeAccountId()).build()
            );
            items.add(buildUserProfileResponse(
                currentAccountId,
                follow.getFolloweeAccountId(),
                profile,
                followedIds
            ));
        }
        return CommunityUserPageResponse.builder()
            .page(safePage)
            .pageSize(safePageSize)
            .hasMore(hasMore)
            .items(items)
            .build();
    }

    private CommunityUserProfileResponse buildUserProfileResponse(Long currentAccountId, Long targetAccountId, AccountProfileEntity profile) {
        long followerCount = readCount(buildCountMap(accountFollowDao.countFollowersByAccountIds(Collections.singletonList(targetAccountId))), targetAccountId);
        long followingCount = readCount(buildCountMap(accountFollowDao.countFollowingByAccountIds(Collections.singletonList(targetAccountId))), targetAccountId);
        long shareCount = readCount(buildCountMap(communityPostDao.countByAccountIds(Collections.singletonList(targetAccountId))), targetAccountId);
        long likeCount = readCount(buildCountMap(communityPostLikeDao.countReceivedLikesByAccountIds(Collections.singletonList(targetAccountId))), targetAccountId);
        boolean self = currentAccountId != null && currentAccountId.equals(targetAccountId);
        boolean followed = !self && accountFollowDao.selectByPair(currentAccountId, targetAccountId).isPresent();
        return buildUserProfileResponse(targetAccountId, profile, followerCount, followingCount, shareCount, likeCount, followed, self);
    }

    private CommunityUserProfileResponse buildUserProfileResponse(
            Long currentAccountId,
            Long targetAccountId,
            AccountProfileEntity profile,
            Set<Long> followedIds
    ) {
        boolean self = currentAccountId != null && currentAccountId.equals(targetAccountId);
        boolean followed = !self && followedIds.contains(targetAccountId);
        return buildUserProfileResponse(
                targetAccountId,
                profile,
                -1,
                -1,
                -1,
                -1,
                followed,
                self
        );
    }

    private CommunityUserProfileResponse buildUserProfileResponse(
        Long targetAccountId,
        AccountProfileEntity profile,
        long followerCount,
        long followingCount,
        long shareCount,
        long likeCount,
        boolean followed,
        boolean self
    ) {
        return CommunityUserProfileResponse.builder()
            .accountId(targetAccountId)
            .nickname(defaultNickname(profile.getNickname()))
            .avatar(defaultAvatar(profile.getAvatar()))
            .gender(profile.getGender() == null ? null : profile.getGender().name())
            .followerCount(followerCount)
            .followingCount(followingCount)
            .shareCount(shareCount)
            .likeCount(likeCount)
            .followedByCurrentUser(followed)
            .self(self)
            .build();
    }

    private CommunityPostResponse toPostResponse(
        CommunityPostEntity entity,
        AccountProfileEntity profile,
        boolean followed,
        long likeCount,
        boolean likedByCurrentUser
    ) {
        String nickname = profile != null && StringUtils.hasText(profile.getNickname())
            ? profile.getNickname().trim()
            : defaultNickname(entity.getNicknameSnapshot());
        String avatar = profile != null && StringUtils.hasText(profile.getAvatar())
            ? profile.getAvatar().trim()
            : defaultAvatar(entity.getAvatarSnapshot());
        String gender = profile != null && profile.getGender() != null
            ? profile.getGender().name()
            : entity.getGenderSnapshot() == null ? null : entity.getGenderSnapshot().name();
        return CommunityPostResponse.builder()
            .postId(entity.getId())
            .accountId(entity.getAccountId())
            .nickname(nickname)
            .avatar(avatar)
            .gender(gender)
            .sharedDate(entity.getSharedDate())
            .goalTitle(entity.getGoalTitle())
            .goalCategory(entity.getGoalCategory())
            .completionStatus(entity.getCompletionStatus())
            .rewardText(entity.getRewardText())
            .createdAt(toMillis(entity.getCreatedAt()))
            .likeCount(likeCount)
            .likedByCurrentUser(likedByCurrentUser)
            .followedByCurrentUser(followed)
            .build();
    }

    private AccountProfileEntity requireCompletedProfile(Long accountId) {
        AccountProfileEntity profile = accountProfileDao.selectByAccountId(accountId)
            .orElseThrow(() -> new BusinessException(MessageCodeEnum.PROFILE_INCOMPLETE));
        if (!StringUtils.hasText(profile.getNickname()) || !StringUtils.hasText(profile.getAvatar())) {
            throw new BusinessException(MessageCodeEnum.PROFILE_INCOMPLETE);
        }
        return profile;
    }

    private AccountProfileEntity requireTargetProfile(Long accountId) {
        return accountProfileDao.selectByAccountId(accountId)
            .orElseThrow(() -> new BusinessException(MessageCodeEnum.ACCOUNT_NOT_FOUND));
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }

    private String defaultNickname(String nickname) {
        return StringUtils.hasText(nickname) ? nickname.trim() : "星星用户";
    }

    private String defaultAvatar(String avatar) {
        return StringUtils.hasText(avatar) ? avatar.trim() : "avatar_cat";
    }

    private Long toMillis(LocalDateTime time) {
        if (time == null) {
            return null;
        }
        return Long.valueOf(time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
    }

    private int normalizePage(Integer page) {
        return page == null || page.intValue() < 1 ? DEFAULT_PAGE : page.intValue();
    }

    private int normalizePageSize(Integer pageSize) {
        return pageSize == null || pageSize.intValue() < 1 ? DEFAULT_PAGE_SIZE : Math.min(pageSize.intValue(), MAX_PAGE_SIZE);
    }

    private Map<Long, AccountProfileEntity> buildProfileMap(List<CommunityPostEntity> posts) {
        List<Long> accountIds = extractPostAccountIds(posts);
        if (accountIds.isEmpty()) {
            return new HashMap<Long, AccountProfileEntity>();
        }
        return buildProfileMapByAccountIds(accountIds);
    }

    private Map<Long, AccountProfileEntity> buildProfileMapByAccountIds(Collection<Long> accountIds) {
        Map<Long, AccountProfileEntity> profileMap = new HashMap<Long, AccountProfileEntity>();
        if (accountIds == null || accountIds.isEmpty()) {
            return profileMap;
        }
        for (AccountProfileEntity profile : accountProfileDao.selectByAccountIds(new ArrayList<Long>(new HashSet<Long>(accountIds)))) {
            profileMap.put(profile.getAccountId(), profile);
        }
        return profileMap;
    }

    private List<Long> extractPostAccountIds(List<CommunityPostEntity> posts) {
        List<Long> accountIds = new ArrayList<Long>(posts.size());
        for (CommunityPostEntity entity : posts) {
            if (entity.getAccountId() != null) {
                accountIds.add(entity.getAccountId());
            }
        }
        return accountIds;
    }

    private List<Long> extractPostIds(List<CommunityPostEntity> posts) {
        List<Long> postIds = new ArrayList<Long>(posts.size());
        for (CommunityPostEntity entity : posts) {
            if (entity.getId() != null) {
                postIds.add(entity.getId());
            }
        }
        return postIds;
    }

    private List<Long> extractAccountIds(List<AccountFollowEntity> followEntities, boolean followerSide) {
        List<Long> accountIds = new ArrayList<Long>(followEntities.size());
        for (AccountFollowEntity entity : followEntities) {
            accountIds.add(followerSide ? entity.getFollowerAccountId() : entity.getFolloweeAccountId());
        }
        return accountIds;
    }

    private Map<Long, Long> buildCountMap(List<AccountMetricCountModel> rows) {
        Map<Long, Long> countMap = new HashMap<Long, Long>();
        if (rows == null) {
            return countMap;
        }
        for (AccountMetricCountModel row : rows) {
            countMap.put(row.getAccountId(), row.getCountValue());
        }
        return countMap;
    }

    private Map<Long, Long> buildPostCountMap(List<PostMetricCountModel> rows) {
        Map<Long, Long> countMap = new HashMap<Long, Long>();
        if (rows == null) {
            return countMap;
        }
        for (PostMetricCountModel row : rows) {
            countMap.put(row.getPostId(), row.getCountValue());
        }
        return countMap;
    }

    private long readCount(Map<Long, Long> countMap, Long accountId) {
        Long value = countMap.get(accountId);
        return value == null ? 0L : value.longValue();
    }

    private long readPostCount(Map<Long, Long> countMap, Long postIdValue) {
        Long value = countMap.get(postIdValue);
        return value == null ? 0L : value.longValue();
    }

    private Set<Long> buildFollowedSet(Long currentAccountId, List<Long> accountIds) {
        if (currentAccountId == null || accountIds == null || accountIds.isEmpty()) {
            return Collections.emptySet();
        }
        return new HashSet<Long>(accountFollowDao.selectFolloweeIdsByFollowerAndTargets(currentAccountId, accountIds));
    }

    private Set<Long> buildLikedPostSet(Long currentAccountId, List<Long> postIds) {
        if (currentAccountId == null || postIds == null || postIds.isEmpty()) {
            return Collections.emptySet();
        }
        return new HashSet<Long>(communityPostLikeDao.selectLikedPostIdsByAccount(currentAccountId, postIds));
    }
}

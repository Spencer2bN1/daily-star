package com.dailystar.entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountSnapshotEntity {

    private Long id;
    private Long accountId;
    private String profileId;
    private String snapshotJson;
    private Long snapshotUpdatedAt;
    private String snapshotHash;
    private Long syncRevision;
    private String lastDeviceId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

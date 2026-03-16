package com.dailystar.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncProfileRequest {

    private String profileId;
    private String snapshotJson;
    private Long snapshotUpdatedAt;
    private String snapshotHash;
    private Long baseRevision;
    private String baseSnapshotHash;
    private String deviceId;
    private String resolveStrategy;
}

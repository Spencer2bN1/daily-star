package com.dailystar.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncProfileResponse {

    private String action;
    private String profileId;
    private String snapshotJson;
    private Long snapshotUpdatedAt;
    private String snapshotHash;
    private Long syncRevision;
    private String message;
    private SyncConflictPayload conflict;
}

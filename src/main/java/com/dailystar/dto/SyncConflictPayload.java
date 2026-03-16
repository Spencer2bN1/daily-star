package com.dailystar.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncConflictPayload {

    private String profileId;
    private Long remoteRevision;
    private Long remoteUpdatedAt;
    private String remoteSnapshotHash;
    private Long localUpdatedAt;
    private String localSnapshotHash;
    private String message;
}

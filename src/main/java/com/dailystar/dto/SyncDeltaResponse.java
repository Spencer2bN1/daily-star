package com.dailystar.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncDeltaResponse {

    private String action;
    private String profileId;
    private Long nextCursor;
    private String message;
    private SyncDeltaConflictPayload conflict;

    @Builder.Default
    private List<SyncRemoteChangeResponse> remoteChanges = new ArrayList<>();
}

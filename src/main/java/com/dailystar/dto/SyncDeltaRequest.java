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
public class SyncDeltaRequest {

    private String profileId;
    private Long lastCursor;
    private String resolveStrategy;

    @Builder.Default
    private List<SyncDeltaChangeRequest> changes = new ArrayList<>();
}

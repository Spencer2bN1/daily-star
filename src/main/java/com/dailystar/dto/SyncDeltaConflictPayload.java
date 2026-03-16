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
public class SyncDeltaConflictPayload {

    private String message;
    private Integer conflictCount;

    @Builder.Default
    private List<String> entityTypes = new ArrayList<>();
}

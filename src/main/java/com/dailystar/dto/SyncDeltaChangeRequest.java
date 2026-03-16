package com.dailystar.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncDeltaChangeRequest {

    private String entityType;
    private String syncId;
    private String operation;
    private String payloadJson;
    private String payloadHash;
    private Long baseVersion;
    private Long clientUpdatedAt;
}

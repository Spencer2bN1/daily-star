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
public class SyncRecordEntity {

    private Long id;
    private Long accountId;
    private String profileId;
    private String entityType;
    private String entitySyncId;
    private String operationType;
    private String payloadJson;
    private String payloadHash;
    private Long serverVersion;
    private Long clientUpdatedAt;
    private LocalDateTime updatedAt;
}

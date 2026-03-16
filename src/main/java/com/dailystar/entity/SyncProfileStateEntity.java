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
public class SyncProfileStateEntity {

    private Long id;
    private Long accountId;
    private String profileId;
    private Long currentCursor;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

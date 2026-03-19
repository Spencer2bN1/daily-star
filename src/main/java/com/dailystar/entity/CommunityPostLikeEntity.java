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
public class CommunityPostLikeEntity {

    private Long id;
    private Long postId;
    private Long accountId;
    private LocalDateTime createdAt;
}

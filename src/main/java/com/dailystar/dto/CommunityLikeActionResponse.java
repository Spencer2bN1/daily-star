package com.dailystar.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityLikeActionResponse {

    private Long postId;
    private Long accountId;
    private boolean likedByCurrentUser;
}

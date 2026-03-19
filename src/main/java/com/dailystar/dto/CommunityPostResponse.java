package com.dailystar.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityPostResponse {

    private Long postId;
    private Long accountId;
    private String nickname;
    private String avatar;
    private String gender;
    private String sharedDate;
    private String goalTitle;
    private String goalCategory;
    private String completionStatus;
    private String rewardText;
    private Long createdAt;
    private long likeCount;
    private boolean likedByCurrentUser;
    private boolean followedByCurrentUser;
}

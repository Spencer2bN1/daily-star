package com.dailystar.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityUserProfileResponse {

    private Long accountId;
    private String nickname;
    private String avatar;
    private String gender;
    private Long followerCount;
    private Long followingCount;
    private Long shareCount;
    private Long likeCount;
    private boolean followedByCurrentUser;
    private boolean self;
}

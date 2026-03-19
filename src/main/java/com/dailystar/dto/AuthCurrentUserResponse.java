package com.dailystar.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthCurrentUserResponse {

    private Long accountId;
    private String mobile;
    private String status;
    private String nickname;
    private String avatar;
    private String gender;
    private long followerCount;
    private long followingCount;
    private long shareCount;
    private long likeCount;
    private boolean profileCompleted;
}

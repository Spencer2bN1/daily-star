package com.dailystar.service;

import com.dailystar.dto.CommunityFeedResponse;
import com.dailystar.dto.CommunityFollowActionResponse;
import com.dailystar.dto.CommunityPostResponse;
import com.dailystar.dto.CommunityShareRequest;
import com.dailystar.dto.CommunityUserPageResponse;
import com.dailystar.dto.CommunityUserProfileResponse;

public interface CommunityService {

    CommunityPostResponse share(Long currentAccountId, CommunityShareRequest request);

    CommunityFeedResponse feed(Long currentAccountId, Integer page, Integer pageSize);

    CommunityUserProfileResponse userProfile(Long currentAccountId, Long targetAccountId);

    CommunityFollowActionResponse follow(Long currentAccountId, Long targetAccountId);

    CommunityFollowActionResponse unfollow(Long currentAccountId, Long targetAccountId);

    CommunityUserPageResponse followers(Long currentAccountId, Long targetAccountId, Integer page, Integer pageSize);

    CommunityUserPageResponse following(Long currentAccountId, Long targetAccountId, Integer page, Integer pageSize);
}

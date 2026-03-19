package com.dailystar.controller;

import com.dailystar.component.AuthContextHolder;
import com.dailystar.dto.CommunityFeedResponse;
import com.dailystar.dto.CommunityFollowActionResponse;
import com.dailystar.dto.CommunityLikeActionResponse;
import com.dailystar.dto.CommunityPostResponse;
import com.dailystar.dto.CommunityShareRequest;
import com.dailystar.dto.CommunityUserPageResponse;
import com.dailystar.dto.CommunityUserProfileResponse;
import com.dailystar.model.ApiResponse;
import com.dailystar.service.CommunityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/community")
@RequiredArgsConstructor
@Tag(name = "Community", description = "社区分享与关注接口")
@SecurityRequirement(name = "bearerAuth")
public class CommunityController {

    private final CommunityService communityService;

    @PostMapping("/share")
    @Operation(summary = "分享今日任务到社区")
    public ApiResponse<CommunityPostResponse> share(@Valid @RequestBody CommunityShareRequest request) {
        return ApiResponse.success(communityService.share(AuthContextHolder.requireAccountId(), request));
    }

    @GetMapping("/feed")
    @Operation(summary = "获取社区动态列表")
    public ApiResponse<CommunityFeedResponse> feed(
        @RequestParam(value = "page", required = false) Integer page,
        @RequestParam(value = "pageSize", required = false) Integer pageSize
    ) {
        return ApiResponse.success(communityService.feed(AuthContextHolder.requireAccountId(), page, pageSize));
    }

    @GetMapping("/users/{accountId}")
    @Operation(summary = "查看社区用户信息")
    public ApiResponse<CommunityUserProfileResponse> userProfile(@PathVariable("accountId") Long accountId) {
        return ApiResponse.success(communityService.userProfile(AuthContextHolder.requireAccountId(), accountId));
    }

    @PostMapping("/users/{accountId}/follow")
    @Operation(summary = "关注用户")
    public ApiResponse<CommunityFollowActionResponse> follow(@PathVariable("accountId") Long accountId) {
        return ApiResponse.success(communityService.follow(AuthContextHolder.requireAccountId(), accountId));
    }

    @DeleteMapping("/users/{accountId}/follow")
    @Operation(summary = "取消关注用户")
    public ApiResponse<CommunityFollowActionResponse> unfollow(@PathVariable("accountId") Long accountId) {
        return ApiResponse.success(communityService.unfollow(AuthContextHolder.requireAccountId(), accountId));
    }

    @PostMapping("/posts/{postId}/like")
    @Operation(summary = "点赞社区分享")
    public ApiResponse<CommunityLikeActionResponse> likePost(@PathVariable("postId") Long postId) {
        return ApiResponse.success(communityService.likePost(AuthContextHolder.requireAccountId(), postId));
    }

    @DeleteMapping("/posts/{postId}/like")
    @Operation(summary = "取消点赞社区分享")
    public ApiResponse<CommunityLikeActionResponse> unlikePost(@PathVariable("postId") Long postId) {
        return ApiResponse.success(communityService.unlikePost(AuthContextHolder.requireAccountId(), postId));
    }

    @GetMapping("/users/{accountId}/followers")
    @Operation(summary = "查看粉丝列表")
    public ApiResponse<CommunityUserPageResponse> followers(
        @PathVariable("accountId") Long accountId,
        @RequestParam(value = "page", required = false) Integer page,
        @RequestParam(value = "pageSize", required = false) Integer pageSize
    ) {
        return ApiResponse.success(communityService.followers(AuthContextHolder.requireAccountId(), accountId, page, pageSize));
    }

    @GetMapping("/users/{accountId}/following")
    @Operation(summary = "查看关注列表")
    public ApiResponse<CommunityUserPageResponse> following(
        @PathVariable("accountId") Long accountId,
        @RequestParam(value = "page", required = false) Integer page,
        @RequestParam(value = "pageSize", required = false) Integer pageSize
    ) {
        return ApiResponse.success(communityService.following(AuthContextHolder.requireAccountId(), accountId, page, pageSize));
    }
}

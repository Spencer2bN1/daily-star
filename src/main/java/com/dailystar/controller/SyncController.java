package com.dailystar.controller;

import com.dailystar.component.AuthContextHolder;
import com.dailystar.dto.SyncDeltaRequest;
import com.dailystar.dto.SyncDeltaResponse;
import com.dailystar.dto.SyncProfileRequest;
import com.dailystar.dto.SyncProfileResponse;
import com.dailystar.model.ApiResponse;
import com.dailystar.service.SyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sync")
@RequiredArgsConstructor
@Tag(name = "Sync", description = "离线档案云同步接口")
public class SyncController {

    private final SyncService syncService;

    @PostMapping("/profile")
    @Operation(summary = "同步当前档案")
    @SecurityRequirement(name = "bearerAuth")
    public ApiResponse<SyncProfileResponse> syncProfile(@RequestBody SyncProfileRequest request) {
        return ApiResponse.success(syncService.syncProfile(AuthContextHolder.requireAccountId(), request));
    }

    @PostMapping("/delta")
    @Operation(summary = "增量同步当前档案")
    @SecurityRequirement(name = "bearerAuth")
    public ApiResponse<SyncDeltaResponse> syncDelta(@RequestBody SyncDeltaRequest request) {
        return ApiResponse.success(syncService.syncDelta(AuthContextHolder.requireAccountId(), request));
    }

    @DeleteMapping("/profile")
    @Operation(summary = "清空云端档案")
    @SecurityRequirement(name = "bearerAuth")
    public ApiResponse<Void> clearProfile() {
        syncService.clearProfile(AuthContextHolder.requireAccountId());
        return ApiResponse.success(null);
    }
}

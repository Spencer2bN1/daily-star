package com.dailystar.controller;

import com.dailystar.component.AuthContextHolder;
import com.dailystar.dto.AuthCurrentUserResponse;
import com.dailystar.dto.AuthLoginRequest;
import com.dailystar.dto.AuthLoginResponse;
import com.dailystar.dto.AuthProfileUpdateRequest;
import com.dailystar.dto.AuthRegisterRequest;
import com.dailystar.model.ApiResponse;
import com.dailystar.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "手机号密码认证接口")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "手机号注册")
    public ApiResponse<AuthLoginResponse> register(@Valid @RequestBody AuthRegisterRequest request) {
        return ApiResponse.success(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "手机号密码登录")
    public ApiResponse<AuthLoginResponse> login(@Valid @RequestBody AuthLoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }

    @GetMapping("/me")
    @Operation(summary = "当前登录用户")
    @SecurityRequirement(name = "bearerAuth")
    public ApiResponse<AuthCurrentUserResponse> currentUser() {
        return ApiResponse.success(authService.currentUser(AuthContextHolder.requireAccountId()));
    }

    @PutMapping("/profile")
    @Operation(summary = "更新当前用户基础信息")
    @SecurityRequirement(name = "bearerAuth")
    public ApiResponse<AuthCurrentUserResponse> updateProfile(@Valid @RequestBody AuthProfileUpdateRequest request) {
        return ApiResponse.success(authService.updateProfile(AuthContextHolder.requireAccountId(), request));
    }
}

package com.dailystar.controller;

import com.dailystar.dto.UserCreateRequest;
import com.dailystar.dto.UserResponse;
import com.dailystar.model.ApiResponse;
import com.dailystar.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "用户示例接口")
public class UserController {

    private final UserService userService;

    @PostMapping
    @Operation(summary = "创建用户")
    public ApiResponse<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
        return ApiResponse.success(userService.createUser(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "按ID查询用户")
    public ApiResponse<UserResponse> getUser(@PathVariable Long id) {
        return ApiResponse.success(userService.getUserById(id));
    }

    @GetMapping
    @Operation(summary = "查询用户列表")
    public ApiResponse<List<UserResponse>> listUsers() {
        return ApiResponse.success(userService.listUsers());
    }
}

package com.dailystar.controller;

import com.dailystar.model.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Collections;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
@Tag(name = "Health", description = "健康检查接口")
public class HealthController {

    @GetMapping("/ping")
    @Operation(summary = "服务存活检查")
    public ApiResponse<Map<String, String>> ping() {
        return ApiResponse.success(Collections.singletonMap("status", "ok"));
    }
}

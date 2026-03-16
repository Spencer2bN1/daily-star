package com.dailystar.model;

import com.dailystar.enums.MessageCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private boolean success;
    private int code;
    private String message;
    private T data;

    public static <T> ApiResponse<T> success(T data) {
        return success(MessageCodeEnum.SUCCESS, data);
    }

    public static <T> ApiResponse<T> success(MessageCodeEnum messageCode, T data) {
        return ApiResponse.<T>builder()
            .success(true)
            .code(messageCode.getCode())
            .message(messageCode.getMessage())
            .data(data)
            .build();
    }

    public static ApiResponse<Void> success(MessageCodeEnum messageCode) {
        return success(messageCode, null);
    }

    private static <T> ApiResponse<T> fail(int code, String message) {
        return ApiResponse.<T>builder()
            .success(false)
            .code(code)
            .message(message)
            .build();
    }

    public static <T> ApiResponse<T> fail(MessageCodeEnum messageCode) {
        return fail(messageCode.getCode(), messageCode.getMessage());
    }

    public static <T> ApiResponse<T> fail(MessageCodeEnum messageCode, String message) {
        return fail(messageCode.getCode(), message);
    }
}

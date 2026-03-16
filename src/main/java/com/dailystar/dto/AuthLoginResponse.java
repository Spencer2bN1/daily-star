package com.dailystar.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthLoginResponse {

    private Long accountId;
    private String mobile;
    private String token;
    private String tokenType;
    private long expiresIn;
}

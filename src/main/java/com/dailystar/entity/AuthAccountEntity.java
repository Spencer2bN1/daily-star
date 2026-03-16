package com.dailystar.entity;

import com.dailystar.enums.AuthAccountStatusEnum;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthAccountEntity {

    private Long id;
    private String mobile;
    private String passwordHash;
    private AuthAccountStatusEnum status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLoginAt;
}

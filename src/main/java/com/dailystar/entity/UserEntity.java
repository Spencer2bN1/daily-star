package com.dailystar.entity;

import com.dailystar.enums.UserStatusEnum;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    private Long id;
    private String username;
    private String nickname;
    private UserStatusEnum status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

package com.dailystar.entity;

import com.dailystar.enums.GenderEnum;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountProfileEntity {

    private Long id;
    private Long accountId;
    private String nickname;
    private String avatar;
    private GenderEnum gender;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

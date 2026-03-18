package com.dailystar.entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountFollowEntity {

    private Long id;
    private Long followerAccountId;
    private Long followeeAccountId;
    private LocalDateTime createdAt;
}

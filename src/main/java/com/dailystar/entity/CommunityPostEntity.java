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
public class CommunityPostEntity {

    private Long id;
    private Long accountId;
    private String nicknameSnapshot;
    private String avatarSnapshot;
    private GenderEnum genderSnapshot;
    private String sharedDate;
    private String goalTitle;
    private String goalCategory;
    private String completionStatus;
    private String rewardText;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

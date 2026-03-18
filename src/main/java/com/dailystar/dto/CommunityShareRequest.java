package com.dailystar.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityShareRequest {

    @NotBlank(message = "分享日期不能为空")
    @Size(max = 16, message = "分享日期格式不正确")
    private String sharedDate;

    @NotBlank(message = "目标名称不能为空")
    @Size(max = 128, message = "目标名称过长")
    private String goalTitle;

    @NotBlank(message = "目标分类不能为空")
    @Size(max = 64, message = "目标分类过长")
    private String goalCategory;

    @NotBlank(message = "完成情况不能为空")
    @Size(max = 32, message = "完成情况格式不正确")
    private String completionStatus;

    @Size(max = 255, message = "奖励内容过长")
    private String rewardText;
}

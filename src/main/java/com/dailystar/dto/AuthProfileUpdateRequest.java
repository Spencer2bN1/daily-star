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
public class AuthProfileUpdateRequest {

    @NotBlank(message = "昵称不能为空")
    @Size(max = 64, message = "昵称长度不能超过64个字符")
    private String nickname;

    @NotBlank(message = "角色不能为空")
    @Size(max = 64, message = "角色长度不能超过64个字符")
    private String avatar;

    @Size(max = 32, message = "性别长度不能超过32个字符")
    private String gender;
}

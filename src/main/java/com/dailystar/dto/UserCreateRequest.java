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
public class UserCreateRequest {

    @NotBlank(message = "username不能为空")
    @Size(max = 64, message = "username长度不能超过64")
    private String username;

    @NotBlank(message = "nickname不能为空")
    @Size(max = 64, message = "nickname长度不能超过64")
    private String nickname;
}

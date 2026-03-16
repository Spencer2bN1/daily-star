package com.dailystar.model;

import com.dailystar.enums.LoginTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginUser {

    private Long accountId;
    private String mobile;
    private LoginTypeEnum loginType;
}

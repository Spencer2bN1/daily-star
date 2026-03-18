package com.dailystar.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountMetricCountModel {

    private Long accountId;
    private Long countValue;
}

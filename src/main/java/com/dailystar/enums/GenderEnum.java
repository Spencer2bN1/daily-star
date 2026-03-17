package com.dailystar.enums;

import java.util.Arrays;

public enum GenderEnum {
    FEMALE,
    MALE,
    UNSPECIFIED;

    public static GenderEnum fromCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        return Arrays.stream(values())
            .filter(item -> item.name().equalsIgnoreCase(code.trim()))
            .findFirst()
            .orElse(null);
    }
}

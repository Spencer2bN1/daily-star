package com.dailystar.enums;

import org.springframework.util.StringUtils;

public enum SyncResolveStrategy {
    NORMAL,
    FORCE_UPLOAD,
    FORCE_DOWNLOAD;

    public static SyncResolveStrategy from(String rawValue) {
        if (!StringUtils.hasText(rawValue)) {
            return NORMAL;
        }
        for (SyncResolveStrategy strategy : values()) {
            if (strategy.name().equalsIgnoreCase(rawValue.trim())) {
                return strategy;
            }
        }
        return NORMAL;
    }
}

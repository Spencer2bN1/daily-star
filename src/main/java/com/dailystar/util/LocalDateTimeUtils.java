package com.dailystar.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class LocalDateTimeUtils {

    public static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private LocalDateTimeUtils() {
    }

    public static String format(LocalDateTime time) {
        return time == null ? null : DEFAULT_FORMATTER.format(time);
    }
}

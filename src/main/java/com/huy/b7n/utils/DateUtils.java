package com.huy.b7n.utils;

import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Date;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class DateUtils {

    public static final String FULL_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    public static final String NORMAL_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String SHORT_TIME_PATTERN = "yyyy-MM-dd";
    public static final String DEFAULT_TIMEZONE_GMT7 = "GMT+7";
    public static final String DDMMYYYY = "ddMMuuuu";

    public static Date now() {
        return new Date();
    }
}

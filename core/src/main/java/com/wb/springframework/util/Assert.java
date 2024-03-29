package com.wb.springframework.util;

/**
 * @author WangBing
 * @date 2023/7/1 19:56
 */
public abstract class Assert {
    public static void state(boolean expression, String message) {
        if (!expression) {
            throw new IllegalStateException(message);
        }
    }

    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }
}

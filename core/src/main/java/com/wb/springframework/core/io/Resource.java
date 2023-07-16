package com.wb.springframework.core.io;

/**
 * @author WangBing
 * @date 2023/6/20 21:21
 */
public interface Resource extends InputStreamSource {

    boolean exists();

    default boolean isReadable() {
        return exists();
    }
}

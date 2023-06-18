package com.wb.springframework.core;

/**
 * @author WangBing
 * @date 2023/6/16 22:10
 */
public class NamedThreadLocal<T> extends ThreadLocal<T> {
    private final String name;

    public NamedThreadLocal(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}

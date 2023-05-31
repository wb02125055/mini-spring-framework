package com.wb.springframework.beans.factory.config;

/**
 * @author WangBing
 * @date 2023/5/27 13:31
 */
public interface Scope {
    Object resolveContextualObject(String key);
}

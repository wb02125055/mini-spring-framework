package com.wb.springframework.beans.factory.config;

import com.wb.springframework.beans.factory.ObjectFactory;

/**
 * @author WangBing
 * @date 2023/5/27 13:31
 */
public interface Scope {

    Object get(String name, ObjectFactory<?> objectFactory);

    Object resolveContextualObject(String key);
}

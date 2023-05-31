package com.wb.springframework.beans.factory;

/**
 * @author WangBing
 * @date 2023/5/27 12:55
 */
public interface FactoryBean<T> {
    T getObject() throws Exception;

    Class<?> getObjectType();

    default boolean isSingleton() {
        return true;
    }
}

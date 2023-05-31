package com.wb.springframework.beans.factory.support;

/**
 * @author WangBing
 * @date 2023/5/21 17:48
 */
public interface SingletonBeanRegistry {
    void registerSingleton(String beanName, Object singletonObject);

    Object getSingleton(String beanName);

    boolean containsSingleton(String beanName);
}

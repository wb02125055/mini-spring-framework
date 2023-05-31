package com.wb.springframework.beans.factory;

import com.wb.springframework.beans.BeansException;
import com.wb.springframework.beans.NoSuchBeanDefinitionException;
import com.wb.springframework.core.ResolvableType;

/**
 * @author WangBing
 * @date 2023/5/20 14:37
 */
public interface BeanFactory {

    String FACTORY_BEAN_PREFIX = "&";

    Object getBean(String name) throws BeansException;

    <T> T getBean(String name, Class<T> requiredType) throws BeansException;

    boolean containsBean(String beanName);

    boolean isSingleton(String name) throws NoSuchBeanDefinitionException;

    boolean isTypeMatch(String name, Class<?> typeToMatch) throws NoSuchBeanDefinitionException;

    boolean isTypeMatch(String name, ResolvableType matchType) throws NoSuchBeanDefinitionException;

    Object getBean(String name, Object... args) throws BeansException;

    String[] getAliases(String name);
}

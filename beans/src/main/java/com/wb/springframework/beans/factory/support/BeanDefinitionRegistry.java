package com.wb.springframework.beans.factory.support;

import com.wb.springframework.beans.BeanDefinitionStoreException;
import com.wb.springframework.beans.NoSuchBeanDefinitionException;
import com.wb.springframework.beans.factory.config.BeanDefinition;
import com.wb.springframework.core.AliasRegistry;

/**
 * @author WangBing
 * @date 2023/5/18 22:20
 */
public interface BeanDefinitionRegistry extends AliasRegistry {

    String[] getBeanDefinitionNames();

    void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) throws BeanDefinitionStoreException;

    BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException;

    boolean containsBeanDefinition(String beanName);

    boolean isBeanNameInUse(String beanName);
}

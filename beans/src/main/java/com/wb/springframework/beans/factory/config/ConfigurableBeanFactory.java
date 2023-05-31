package com.wb.springframework.beans.factory.config;

import com.wb.springframework.beans.NoSuchBeanDefinitionException;
import com.wb.springframework.beans.factory.HierarchicalBeanFactory;
import com.wb.springframework.beans.factory.support.SingletonBeanRegistry;

/**
 * @author WangBing
 * @date 2023/5/21 21:34
 */
public interface ConfigurableBeanFactory extends HierarchicalBeanFactory, SingletonBeanRegistry {

    String SCOPE_SINGLETON = "singleton";

    String SCOPE_PROTOTYPE = "prototype";

    void registerScope(String scopeName, Scope scope);

    Scope getRegisteredScope(String scopeName);

    boolean isFactoryBean(String name) throws NoSuchBeanDefinitionException;

    void registerDependentBean(String beanName, String dependentBeanName);
}

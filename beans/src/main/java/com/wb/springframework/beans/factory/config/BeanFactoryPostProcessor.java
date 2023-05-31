package com.wb.springframework.beans.factory.config;

import com.wb.springframework.beans.BeansException;

/**
 * @author WangBing
 * @date 2023/5/21 20:14
 */
@FunctionalInterface
public interface BeanFactoryPostProcessor {

    void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException;
}

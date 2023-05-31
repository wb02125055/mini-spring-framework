package com.wb.springframework.beans.factory.support;

import com.wb.springframework.beans.BeansException;
import com.wb.springframework.beans.factory.config.BeanFactoryPostProcessor;

/**
 * @author WangBing
 * @date 2023/5/21 20:38
 */
public interface BeanDefinitionRegistryPostProcessor extends BeanFactoryPostProcessor {

    void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException;
}

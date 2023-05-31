package com.wb.springframework.context.annotation;

import com.wb.springframework.beans.BeansException;
import com.wb.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import com.wb.springframework.beans.factory.support.BeanDefinitionRegistry;
import com.wb.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import com.wb.springframework.core.Ordered;
import com.wb.springframework.core.PriorityOrdered;

/**
 * @author WangBing
 * @date 2023/5/21 20:37
 */
public class ConfigurationClassPostProcessor implements BeanDefinitionRegistryPostProcessor, PriorityOrdered {
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {

    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}

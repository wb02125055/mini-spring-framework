package com.wb.springframework.beans.factory.support;

import com.wb.springframework.beans.factory.config.BeanDefinition;

/**
 * @author WangBing
 * @date 2023/5/21 14:32
 */
public interface BeanNameGenerator {

    String generateBeanName(BeanDefinition beanDefinition, BeanDefinitionRegistry registry);
}

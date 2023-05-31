package com.wb.springframework.beans.factory.support;

import com.wb.springframework.beans.factory.config.BeanDefinitionHolder;

/**
 * @author WangBing
 * @date 2023/5/21 17:30
 */
public abstract class BeanDefinitionReaderUtils {
    public static void registerBeanDefinition(BeanDefinitionHolder beanDefinitionHolder, BeanDefinitionRegistry registry) {
        String beanName = beanDefinitionHolder.getBeanName();
        // 注册配置类对应的bean定义
        registry.registerBeanDefinition(beanName, beanDefinitionHolder.getBeanDefinition());
        String[] aliases = beanDefinitionHolder.getAliases();
        if (null != aliases) {
            for (String alias : aliases) {
                registry.registerAlias(beanName, alias);
            }
        }
    }
}

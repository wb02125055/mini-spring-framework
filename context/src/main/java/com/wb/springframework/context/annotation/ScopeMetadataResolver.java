package com.wb.springframework.context.annotation;

import com.wb.springframework.beans.factory.config.BeanDefinition;

/**
 * @author WangBing
 * @date 2023/5/21 14:26
 */
@FunctionalInterface
public interface ScopeMetadataResolver {

    ScopeMetadata resolveScopeMetadata(BeanDefinition beanDefinition);
}

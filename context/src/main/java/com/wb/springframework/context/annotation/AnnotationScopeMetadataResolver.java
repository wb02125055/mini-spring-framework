package com.wb.springframework.context.annotation;

import com.wb.springframework.beans.factory.config.BeanDefinition;

/**
 * @author WangBing
 * @date 2023/5/21 14:28
 */
public class AnnotationScopeMetadataResolver implements ScopeMetadataResolver {
    @Override
    public ScopeMetadata resolveScopeMetadata(BeanDefinition beanDefinition) {
        ScopeMetadata metadata = new ScopeMetadata();
        // TODO parse scope
        metadata.setScopeName("singleton");
        return metadata;
    }
}

package com.wb.springframework.context.annotation;

import com.wb.springframework.beans.factory.config.BeanDefinition;

/**
 * @author WangBing
 * @date 2023/5/21 14:24
 */
public class ScopeMetadata {
    private String scopeName = BeanDefinition.SCOPE_SINGLETON;

    public String getScopeName() {
        return scopeName;
    }

    public void setScopeName(String scopeName) {
        this.scopeName = scopeName;
    }
}

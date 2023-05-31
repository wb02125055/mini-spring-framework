package com.wb.springframework.beans.factory.config;

/**
 * @author WangBing
 * @date 2023/5/27 13:29
 */
public class BeanExpressionContext {
    private final ConfigurableBeanFactory beanFactory;
    private final Scope scope;

    public BeanExpressionContext(ConfigurableBeanFactory beanFactory, Scope scope) {
        this.beanFactory = beanFactory;
        this.scope = scope;
    }

    public ConfigurableBeanFactory getBeanFactory() {
        return beanFactory;
    }

    public Scope getScope() {
        return scope;
    }

    public boolean containsObject(String key) {
        return this.beanFactory.containsBean(key) ||
                (this.scope != null && this.scope.resolveContextualObject(key) != null);
    }
}

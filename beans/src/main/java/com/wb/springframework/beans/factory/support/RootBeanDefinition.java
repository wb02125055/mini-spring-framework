package com.wb.springframework.beans.factory.support;

import com.wb.springframework.beans.factory.config.BeanDefinition;
import com.wb.springframework.core.ResolvableType;

/**
 * @author WangBing
 * @date 2023/5/18 22:22
 */
public class RootBeanDefinition extends AbstractBeanDefinition {

    protected volatile ResolvableType targetType;

    protected volatile Class<?> resolvedTargetType;

    protected volatile ResolvableType factoryMethodReturnType;

    public RootBeanDefinition() {
        super();
    }

    public RootBeanDefinition(RootBeanDefinition original) {
        super(original);
    }

    protected RootBeanDefinition(BeanDefinition original) {
        super(original);
    }

    public RootBeanDefinition(Class<?> beanClass) {
        super();
        setBeanClass(beanClass);
    }

    public void setTargetType(ResolvableType targetType) {
        this.targetType = targetType;
    }

    public Class<?> getTargetType() {
        if (this.resolvedTargetType != null) {
            return this.resolvedTargetType;
        }
        ResolvableType targetType = this.targetType;
        return null != targetType ? targetType.resolve() : null;
    }

    @Override
    protected RootBeanDefinition cloneBeanDefinition() {
        return new RootBeanDefinition(this);
    }
}

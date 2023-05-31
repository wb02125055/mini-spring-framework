package com.wb.springframework.beans.factory.support;

import com.wb.springframework.beans.factory.config.BeanDefinition;

/**
 * @author WangBing
 * @date 2023/5/21 09:04
 */
public class GenericBeanDefinition extends AbstractBeanDefinition {

    public GenericBeanDefinition() {
        super();
    }

    public GenericBeanDefinition(BeanDefinition original) {
        super(original);
    }

    @Override
    protected AbstractBeanDefinition cloneBeanDefinition() {
        return new GenericBeanDefinition(this);
    }
}

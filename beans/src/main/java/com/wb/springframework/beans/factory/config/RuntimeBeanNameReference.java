package com.wb.springframework.beans.factory.config;

/**
 * @author WangBing
 * @date 2023/5/28 22:33
 */
public class RuntimeBeanNameReference implements BeanReference {

    private final String beanName;

    private Object source;

    public RuntimeBeanNameReference(String beanName) {
        this.beanName = beanName;
    }

    public void setSource(Object source) {
        this.source = source;
    }

    @Override
    public Object getSource() {
        return this.source;
    }

    @Override
    public String getBeanName() {
        return this.beanName;
    }
}

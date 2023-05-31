package com.wb.springframework.beans.factory.config;

/**
 * @author WangBing
 * @date 2023/5/28 22:10
 */
public class RuntimeBeanReference implements BeanReference {

    private final String beanName;

    private Object source;

    public RuntimeBeanReference(String beanName) {
        this.beanName = beanName;
    }

    @Override
    public Object getSource() {
        return this.source;
    }

    public void setSource(Object source) {
        this.source = source;
    }

    @Override
    public String getBeanName() {
        return this.beanName;
    }
}

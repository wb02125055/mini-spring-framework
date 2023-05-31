package com.wb.springframework.beans.factory;

import com.wb.springframework.beans.BeansException;

/**
 * @author WangBing
 * @date 2023/5/27 22:20
 */
public class BeanNoOfRequiredTypeException extends BeansException {
    private final String beanName;
    private final Class<?> requiredType;
    private final Class<?> actualType;

    public BeanNoOfRequiredTypeException(String beanName, Class<?> requiredType, Class<?> actualType) {
        super(beanName);
        this.beanName = beanName;
        this.requiredType = requiredType;
        this.actualType = actualType;
    }

    public BeanNoOfRequiredTypeException(String msg, String beanName, Class<?> requiredType, Class<?> actualType) {
        super(msg);
        this.beanName = beanName;
        this.requiredType = requiredType;
        this.actualType = actualType;
    }

    public BeanNoOfRequiredTypeException(String msg, Throwable cause, String beanName, Class<?> requiredType, Class<?> actualType) {
        super(msg, cause);
        this.beanName = beanName;
        this.requiredType = requiredType;
        this.actualType = actualType;
    }

    public String getBeanName() {
        return beanName;
    }

    public Class<?> getRequiredType() {
        return requiredType;
    }

    public Class<?> getActualType() {
        return actualType;
    }
}

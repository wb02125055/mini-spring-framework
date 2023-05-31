package com.wb.springframework.beans.factory;

/**
 * @author WangBing
 * @date 2023/5/27 22:16
 */
public class BeanIsNotAFactoryBeanException extends BeanNoOfRequiredTypeException {
    public BeanIsNotAFactoryBeanException(String name, Class<?> actualType) {
        super(name, FactoryBean.class, actualType);
    }
}

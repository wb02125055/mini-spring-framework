package com.wb.springframework.beans;

/**
 * @author WangBing
 * @date 2023/5/28 13:56
 */
public interface BeanWrapper extends ConfigurablePropertyAccessor {
    Object getWrappedInstance();

    Class<?> getWrappedClass();
}

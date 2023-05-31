package com.wb.springframework.beans;

/**
 * @author WangBing
 * @date 2023/5/28 21:15
 */
public interface PropertyAccessor {
    void setPropertyValues(PropertyValues pvs) throws BeansException;
}

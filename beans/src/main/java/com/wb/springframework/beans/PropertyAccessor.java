package com.wb.springframework.beans;

/**
 * @author WangBing
 * @date 2023/5/28 21:15
 */
public interface PropertyAccessor {
    void setPropertyValue(PropertyValue propertyValue) throws BeansException;

    void setPropertyValue(String propertyName, Object propertyValue) throws BeansException;
    void setPropertyValues(PropertyValues pvs) throws BeansException;

    void setPropertyValues(PropertyValues pvs, boolean ignoreUnknown, boolean ignoreInvalid)
            throws BeansException;
}

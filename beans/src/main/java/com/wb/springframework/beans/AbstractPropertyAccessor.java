package com.wb.springframework.beans;

import java.util.Arrays;
import java.util.List;

/**
 * @author WangBing
 * @date 2023/5/28 21:17
 */
public abstract class AbstractPropertyAccessor implements ConfigurablePropertyAccessor {
    @Override
    public void setPropertyValue(PropertyValue propertyValue) throws BeansException {
        setPropertyValue(propertyValue.getName(), propertyValue.getValue());
    }

    @Override
    public void setPropertyValues(PropertyValues pvs) throws BeansException {
        setPropertyValues(pvs, false, false);
    }


    @Override
    public void setPropertyValues(PropertyValues pvs, boolean ignoreUnknown, boolean ignoreInvalid)
        throws BeansException {

        List<PropertyValue> propertyValues = pvs instanceof MutablePropertyValues ?
                ((MutablePropertyValues) pvs).getPropertyValueList()
                : Arrays.asList(pvs.getPropertyValues());

        for (PropertyValue pv : propertyValues) {
            try {
                setPropertyValue(pv);
            } catch (Exception e) {
                if (!ignoreUnknown || !ignoreInvalid) {
                    throw e;
                }
            }
        }
    }

    public abstract void setPropertyValue(String propertyName, Object propertyValue) throws BeansException;
}

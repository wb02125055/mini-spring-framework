package com.wb.springframework.beans;

import com.wb.springframework.util.ObjectUtils;

/**
 * @author WangBing
 * @date 2023/5/28 13:58
 */
public abstract class AbstractNestablePropertyAccessor extends AbstractPropertyAccessor {

    Object wrappedObject;

    public AbstractNestablePropertyAccessor(Object object) {
        setWrappedInstance(object);
    }

    public void setWrappedInstance(Object object) {
        this.wrappedObject = ObjectUtils.unwrapOptional(object);
    }

    public final Object getWrappedInstance() {
        return this.wrappedObject;
    }

    public final Class<?> getWrappedClass() {
        return getWrappedInstance().getClass();
    }

    @Override
    public void setPropertyValue(String propertyName, Object propertyValue) throws BeansException {
        // TODO setPropertyValue
    }
}

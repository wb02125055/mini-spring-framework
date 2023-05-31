package com.wb.springframework.beans;

import com.wb.springframework.core.AttributeAccessorSupport;

/**
 * @author WangBing
 * @date 2023/5/7 10:43
 */
public class BeanMetadataAttributeAccessor extends AttributeAccessorSupport implements BeanMetadataElement {

    private Object source;


    public void setSource(Object source) {
        this.source = source;
    }


    @Override
    public Object getSource() {
        return this.source;
    }

    public void addMetadataAttribute(BeanMetadataAttribute attribute) {
        super.setAttribute(attribute.getName(), attribute);
    }

    public BeanMetadataAttribute getMetadataAttribute(String name) {
        return (BeanMetadataAttribute) super.getAttribute(name);
    }

    @Override
    public void setAttribute(String name, Object value) {
        super.setAttribute(name, value);
    }

    @Override
    public Object getAttribute(String name) {
        BeanMetadataAttribute attribute = (BeanMetadataAttribute) super.getAttribute(name);
        return null != attribute ? attribute.getValue() : null;
    }
}

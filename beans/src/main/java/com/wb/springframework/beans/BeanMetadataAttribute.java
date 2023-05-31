package com.wb.springframework.beans;

import com.wb.springframework.util.ObjectUtils;

/**
 * @author WangBing
 * @date 2023/5/7 10:47
 */
public class BeanMetadataAttribute implements BeanMetadataElement {

    private final String name;
    private final Object value;
    private Object source;

    public BeanMetadataAttribute(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public void setSource(Object source) {
        this.source = source;
    }

    @Override
    public Object getSource() {
        return this.source;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BeanMetadataAttribute)) {
            return false;
        }
        BeanMetadataAttribute other = (BeanMetadataAttribute) o;
        return this.name.equals(other.name) &&
                ObjectUtils.nullSafeEquals(this.value, other.value) &&
                ObjectUtils.nullSafeEquals(this.source, other.source);
    }

    @Override
    public int hashCode() {
        return this.name.hashCode() * 29 + ObjectUtils.nullSafeHashCode(this.value);
    }

    @Override
    public String toString() {
        return "metadata attribute '" + this.name + "'";
    }
}

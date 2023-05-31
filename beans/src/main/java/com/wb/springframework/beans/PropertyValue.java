package com.wb.springframework.beans;

import com.wb.springframework.util.ObjectUtils;

import java.io.Serializable;

/**
 * @author WangBing
 * @date 2023/5/7 10:39
 */
public class PropertyValue extends BeanMetadataAttributeAccessor implements Serializable {

    private final String name;

    private final Object value;

    private boolean optional = false;

    private boolean converted = false;

    private Object convertedValue;

    volatile Boolean conversionNecessary;

    transient volatile Object resolvedTokens;

    public PropertyValue(String name, Object value) {
        this.name = name;
        this.value = value;
    }
    public PropertyValue(PropertyValue original) {
        this.name = original.getName();
        this.value = original.getValue();
        this.optional = original.isOptional();
        this.converted = original.isConverted();
        this.convertedValue = original.convertedValue;
        this.conversionNecessary = original.conversionNecessary;
        this.resolvedTokens = original.resolvedTokens;
        setSource(original.getSource());
        copyAttributeFrom(original);
    }

    public PropertyValue(PropertyValue original, Object newValue) {
        this.name = original.getName();
        this.value = newValue;
        this.optional = original.isOptional();
        this.conversionNecessary = original.conversionNecessary;
        this.resolvedTokens = original.resolvedTokens;
        setSource(original);
        copyAttributeFrom(original);
    }

    public PropertyValue getOriginalPropertyValue() {
        PropertyValue original = this;
        Object source = getSource();
        while (source instanceof PropertyValue && source != original) {
            original = (PropertyValue) source;
            source = original.getSource();
        }
        return original;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public synchronized boolean isConverted() {
        return converted;
    }

    public void setConverted(boolean converted) {
        this.converted = converted;
    }

    public synchronized Object getConvertedValue() {
        return convertedValue;
    }

    public synchronized void setConvertedValue(Object convertedValue) {
        this.convertedValue = convertedValue;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof PropertyValue)) {
            return false;
        }
        PropertyValue otherPropertyValue = (PropertyValue) other;
        return this.name.equals(otherPropertyValue.name)
                && ObjectUtils.nullSafeEquals(this.value, otherPropertyValue.value)
                && ObjectUtils.nullSafeEquals(getSource(), otherPropertyValue.getSource());
    }

    @Override
    public int hashCode() {
        return this.name.hashCode() * 29 + ObjectUtils.nullSafeHashCode(this.value);
    }

    @Override
    public String toString() {
        return "bean property '" + this.name + "'";
    }
}

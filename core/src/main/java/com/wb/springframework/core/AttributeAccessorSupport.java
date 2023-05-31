package com.wb.springframework.core;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author WangBing
 * @date 2023/5/7 10:31
 */
public abstract class AttributeAccessorSupport implements AttributeAccessor, Serializable {

    private final Map<String, Object> attributes = new LinkedHashMap<>();

    @Override
    public void setAttribute(String name, Object value) {
        if (Objects.nonNull(value)) {
            this.attributes.put(name, value);
        } else {
            removeAttribute(name);
        }
    }

    @Override
    public Object getAttribute(String name) {
        return this.attributes.get(name);
    }

    @Override
    public Object removeAttribute(String name) {
        return this.attributes.remove(name);
    }

    @Override
    public boolean hasAttribute(String name) {
        return this.attributes.containsKey(name);
    }

    @Override
    public String[] attributeNames() {
        return this.attributes.keySet().toArray(new String[0]);
    }
    protected void copyAttributeFrom(AttributeAccessor source) {
        String[] attributeNames = source.attributeNames();
        for (String attributeName : attributeNames) {
            setAttribute(attributeName, source.getAttribute(attributeName));
        }
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof AttributeAccessorSupport &&
                this.attributes.equals(((AttributeAccessorSupport) obj).attributes);
    }

    @Override
    public int hashCode() {
        return this.attributes.hashCode();
    }
}

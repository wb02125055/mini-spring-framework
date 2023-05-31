package com.wb.springframework.core;

/**
 * @author WangBing
 * @date 2023/5/4 22:31
 */
public interface AttributeAccessor {
    /**
     * 设置属性的值，name-value，自定义bean中的属性值
     * @param name 属性名称
     * @param value 属性值
     */
    void setAttribute(String name, Object value);

    /**
     *
     * @param name
     * @return
     */
    Object getAttribute(String name);

    Object removeAttribute(String name);

    boolean hasAttribute(String name);

    String[] attributeNames();
}

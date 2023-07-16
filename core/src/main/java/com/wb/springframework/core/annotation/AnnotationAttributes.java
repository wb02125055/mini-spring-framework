package com.wb.springframework.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author WangBing
 * @date 2023/7/1 20:28
 */
public class AnnotationAttributes extends LinkedHashMap<String, Object> {


    private static final String UNKNOWN = "unknown";

    private final Class<? extends Annotation> annotationType;

    final String displayName;

    boolean validated = false;

    public AnnotationAttributes() {
        this.annotationType = null;
        displayName = UNKNOWN;
    }

    public AnnotationAttributes(Class<? extends Annotation> annotationType) {
        this.annotationType = annotationType;
        this.displayName = annotationType.getName();
    }

    public AnnotationAttributes(Map<String, Object> map) {
        super(map);
        this.annotationType = null;
        this.displayName = UNKNOWN;
    }

    public AnnotationAttributes(AnnotationAttributes other) {
        super(other);
        this.annotationType = other.annotationType;
        this.displayName = other.displayName;
        this.validated = other.validated;
    }

    public static AnnotationAttributes fromMap(Map<String, Object> map) {
        if (map == null) {
            return null;
        }
        if (map instanceof AnnotationAttributes) {
            return (AnnotationAttributes) map;
        }
        return new AnnotationAttributes(map);
    }

    public Class<? extends Annotation> annotationType() {
        return this.annotationType;
    }

    public boolean getBoolean(String attributeName) {
        return getRequiredAttribute(attributeName, Boolean.class);
    }

    @SuppressWarnings("unchecked")
    public <T> Class<? extends T> getClass(String attributeName) {
        return getRequiredAttribute(attributeName, Class.class);
    }

    public String[] getStringArray(String attributeName) {
        return getRequiredAttribute(attributeName, String[].class);
    }


    @SuppressWarnings("unchecked")
    private <T> T getRequiredAttribute(String attributeName, Class<T> expectedType) {
        Object value = get(attributeName);
        if (!expectedType.isInstance(value) && expectedType.isArray() &&
            expectedType.getComponentType().isInstance(value)) {
            Object array = Array.newInstance(expectedType.getComponentType(), 1);
            Array.set(array, 0, value);
            value = array;
        }
        return (T) value;
    }





















}

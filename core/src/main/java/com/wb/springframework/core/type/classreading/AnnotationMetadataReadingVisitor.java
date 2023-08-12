package com.wb.springframework.core.type.classreading;

import com.wb.springframework.core.type.AnnotationMetadata;

import java.util.Map;

/**
 * @author WangBing
 * @date 2023/7/26 22:45
 */
public class AnnotationMetadataReadingVisitor extends ClassMetadataReadingVisitor implements AnnotationMetadata {

    private final ClassLoader classLoader;

    public AnnotationMetadataReadingVisitor(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public boolean isAnnotated(String annotationName) {
        return false;
    }

    @Override
    public Map<String, Object> getAnnotationAttributes(String annotationName, boolean classValuesAsString) {
        return null;
    }
}

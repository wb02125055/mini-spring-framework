package com.wb.springframework.core.type;

import com.wb.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * @author WangBing
 * @date 2023/6/17 14:53
 */
public class StandardAnnotationMetadata extends StandardClassMetadata implements AnnotationMetadata {

    private final Annotation[] annotations;

    private final boolean nestedAnnotationsAsMap;

    public StandardAnnotationMetadata(Class<?> instrospectedClass) {
        this(instrospectedClass, false);
    }
    public StandardAnnotationMetadata(Class<?> introspectedClass, boolean nestedAnnotationAsMap) {
        super(introspectedClass);
        this.annotations = introspectedClass.getAnnotations();
        this.nestedAnnotationsAsMap = nestedAnnotationAsMap;
    }

    @Override
    public boolean isAnnotated(String annotationName) {
        return this.annotations.length > 0 &&
                AnnotatedElementUtils.isAnnotated(getIntrospectedClass(), annotationName);
    }

    @Override
    public Map<String, Object> getAnnotationAttributes(String annotationName, boolean classValuesAsString) {
        return this.annotations.length > 0 ? AnnotatedElementUtils.getMergedAnnotationAttributes(
                getIntrospectedClass(), annotationName, classValuesAsString, this.nestedAnnotationsAsMap
        ) : null;
    }



















}

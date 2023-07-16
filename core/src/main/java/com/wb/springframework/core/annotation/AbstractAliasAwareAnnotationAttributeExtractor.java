package com.wb.springframework.core.annotation;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

/**
 * @author WangBing
 * @date 2023/7/5 22:56
 */
abstract class AbstractAliasAwareAnnotationAttributeExtractor<S> implements AnnotationAttributeExtractor<S> {

    private final Class<? extends Annotation> annotationType;

    private final Object annotatedElement;

    private final S source;

    private final Map<String, List<String>> attributeAliasMap;

    public AbstractAliasAwareAnnotationAttributeExtractor(Class<? extends Annotation> annotationType,
                                                          Object annotatedElement, S source) {
        this.annotationType = annotationType;
        this.annotatedElement = annotatedElement;
        this.source = source;
        this.attributeAliasMap = AnnotationUtils.getAttributeAliasMap(annotationType);
    }

















}

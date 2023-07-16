package com.wb.springframework.core.annotation;

import java.lang.annotation.Annotation;

/**
 * @author WangBing
 * @date 2023/7/5 22:55
 */
class DefaultAnnotationAttributeExtractor extends AbstractAliasAwareAnnotationAttributeExtractor<Annotation> {

    DefaultAnnotationAttributeExtractor(Annotation annotation, Object annotatedElement) {
        super(annotation.annotationType(), annotatedElement, annotation);
    }
}

package com.wb.springframework.core.type.filter;

import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;

/**
 * @author WangBing
 * @date 2023/7/16 18:42
 */
public class AnnotationTypeFilter extends AbstractTypeHierarchyTraversingFilter {


    private final Class<? extends Annotation> annotationType;

    private final boolean considerMetaAnnotations;


    public AnnotationTypeFilter(Class<? extends Annotation> annotationType) {
        this(annotationType, true, false);
    }


    // consider: 考虑，认为
    public AnnotationTypeFilter(Class<? extends Annotation> annotationType, boolean considerMetaAnnotations, boolean considerInterfaces) {
        super(annotationType.isAnnotationPresent(Inherited.class), considerInterfaces);
        this.annotationType = annotationType;
        this.considerMetaAnnotations = considerMetaAnnotations;
    }

    public final Class<? extends Annotation> getAnnotationType() {
        return annotationType;
    }

}

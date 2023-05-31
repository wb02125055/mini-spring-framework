package com.wb.springframework.beans.factory.annotation;

import com.wb.springframework.beans.factory.support.GenericBeanDefinition;
import com.wb.springframework.core.type.AnnotationMetadata;

/**
 * @author WangBing
 * @date 2023/5/21 09:03
 */
public class AnnotatedGenericBeanDefinition extends GenericBeanDefinition implements AnnotatedBeanDefinition {

    public AnnotatedGenericBeanDefinition(Class<?> beanClass) {
        setBeanClass(beanClass);
    }

    @Override
    public AnnotationMetadata getMetadata() {
        return null;
    }
}

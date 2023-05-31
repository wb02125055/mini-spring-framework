package com.wb.springframework.beans.factory.annotation;

import com.wb.springframework.beans.factory.config.BeanDefinition;
import com.wb.springframework.core.type.AnnotationMetadata;

/**
 * @author WangBing
 * @date 2023/5/21 14:36
 */
public interface AnnotatedBeanDefinition extends BeanDefinition {

    AnnotationMetadata getMetadata();
}

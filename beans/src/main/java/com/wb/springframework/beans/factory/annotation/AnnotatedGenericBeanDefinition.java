package com.wb.springframework.beans.factory.annotation;

import com.wb.springframework.beans.factory.support.GenericBeanDefinition;
import com.wb.springframework.core.type.AnnotationMetadata;
import com.wb.springframework.core.type.StandardAnnotationMetadata;

/**
 * @author WangBing
 * @date 2023/5/21 09:03
 */
public class AnnotatedGenericBeanDefinition extends GenericBeanDefinition implements AnnotatedBeanDefinition {

    /**
     * 提供获取类上注解元数据的接口方法
     */
    private final AnnotationMetadata metadata;

    public AnnotatedGenericBeanDefinition(Class<?> beanClass) {
        setBeanClass(beanClass);
        this.metadata = new StandardAnnotationMetadata(beanClass, true);
    }

    @Override
    public final AnnotationMetadata getMetadata() {
        return metadata;
    }
}

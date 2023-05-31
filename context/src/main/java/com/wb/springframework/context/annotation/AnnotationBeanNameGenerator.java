package com.wb.springframework.context.annotation;

import com.wb.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import com.wb.springframework.beans.factory.config.BeanDefinition;
import com.wb.springframework.beans.factory.support.BeanDefinitionRegistry;
import com.wb.springframework.beans.factory.support.BeanNameGenerator;
import com.wb.springframework.util.ClassUtils;
import com.wb.springframework.util.StringUtils;

import java.beans.Introspector;

/**
 * @author WangBing
 * @date 2023/5/21 14:33
 */
public class AnnotationBeanNameGenerator implements BeanNameGenerator {
    @Override
    public String generateBeanName(BeanDefinition beanDefinition, BeanDefinitionRegistry registry) {
        if (beanDefinition instanceof AnnotatedBeanDefinition) {
            String beanName = determineBeanNameFromAnnotation((AnnotatedBeanDefinition) beanDefinition);
            if (StringUtils.hasText(beanName)) {
                return beanName;
            }
        }
        return buildDefaultBeanName(beanDefinition, registry);
    }

    protected String determineBeanNameFromAnnotation(AnnotatedBeanDefinition beanDefinition) {
        return null;
    }
    protected String buildDefaultBeanName(BeanDefinition beanDefinition, BeanDefinitionRegistry registry) {
        return buildDefaultBeanName(beanDefinition);
    }

    protected String buildDefaultBeanName(BeanDefinition beanDefinition) {
        String beanClassName = beanDefinition.getBeanClassName();
        String shortClassName = ClassUtils.getShortName(beanClassName);
        return Introspector.decapitalize(shortClassName);
    }
}

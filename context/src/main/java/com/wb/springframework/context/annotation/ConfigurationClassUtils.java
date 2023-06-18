package com.wb.springframework.context.annotation;

import com.wb.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import com.wb.springframework.beans.factory.config.BeanDefinition;
import com.wb.springframework.beans.factory.support.AbstractBeanDefinition;
import com.wb.springframework.core.Conventions;
import com.wb.springframework.core.type.AnnotationMetadata;
import com.wb.springframework.core.type.StandardAnnotationMetadata;
import com.wb.springframework.core.type.classreading.MetadataReader;
import com.wb.springframework.core.type.classreading.MetadataReaderFactory;
import com.wb.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author WangBing
 * @date 2023/6/16 22:42
 */
public abstract class ConfigurationClassUtils {

    private static final String CONFIGURATION_CLASS_FULL = "full";

    private static final String CONFIGURATION_CLASS_LITE = "lite";

    private static final String CONFIGURATION_CLASS_ATTRIBUTE = Conventions.getQualifiedAttributeName(ConfigurationClassPostProcessor.class, "configurationClass");

    private static final String ORDER_ATTRIBUTE = Conventions.getQualifiedAttributeName(ConfigurationClassPostProcessor.class, "order");

    private static final Set<String> candidateIndicators = new HashSet<>(8);

    static {
        candidateIndicators.add(Component.class.getName());
        candidateIndicators.add(ComponentScan.class.getName());
    }

    public static boolean isFullConfigurationClass(BeanDefinition beanDef) {
        return CONFIGURATION_CLASS_FULL.equals(beanDef.getAttribute(CONFIGURATION_CLASS_ATTRIBUTE));
    }

    public static boolean isLiteConfigurationClass(BeanDefinition beanDef) {
        return CONFIGURATION_CLASS_LITE.equals(beanDef.getAttribute(CONFIGURATION_CLASS_ATTRIBUTE));
    }

    public static boolean checkConfigurationClassCandidate(BeanDefinition beanDef, MetadataReaderFactory metadataReaderFactory) {

        String className = beanDef.getBeanClassName();
        if (className == null || beanDef.getFactoryMethodName() != null) {
            return false;
        }
        System.out.println("开发到这里了");

        AnnotationMetadata metadata;
        if (beanDef instanceof AnnotatedBeanDefinition &&
            className.equals(((AnnotatedBeanDefinition) beanDef).getMetadata().getClassName())) {
            metadata = ((AnnotatedBeanDefinition) beanDef).getMetadata();
        } else if (beanDef instanceof AbstractBeanDefinition
                && ((AbstractBeanDefinition) beanDef).hasBeanClass()) {
            // RootBeanDefinition是AbstractBeanDefinition的子类，如果是RootBeanDefinition类型的，会执行这个逻辑
            Class<?> beanClass = ((AbstractBeanDefinition) beanDef).getBeanClass();
            metadata = new StandardAnnotationMetadata(beanClass, true);
        } else {
            try {
                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(className);
                metadata = metadataReader.getAnnotationMetadata();
            } catch (IOException e) {
                return false;
            }
        }
        // 当前的bean定义是否为一个配置类，判断依据是：是否标注有@Configuration注解
        if (isFullConfigurationCandidate(metadata)) {
            beanDef.setAttribute(CONFIGURATION_CLASS_ATTRIBUTE, CONFIGURATION_CLASS_FULL);
        } else if (isLiteConfigurationCandidate(metadata)) {
            beanDef.setAttribute(CONFIGURATION_CLASS_ATTRIBUTE, CONFIGURATION_CLASS_LITE);
        } else {
            return false;
        }
        Integer order = getOrder(metadata);
        if (order != null) {
            // 设置bean定义的优先级
            beanDef.setAttribute(ORDER_ATTRIBUTE, order);
        }
        return true;
    }

    private static Integer getOrder(AnnotationMetadata metadata) {
        // TODO
        return null;
    }

    public static boolean isLiteConfigurationCandidate(AnnotationMetadata metadata) {
        // 如果当前的bean定义对应的class是一个接口，则不是配置类
        if (metadata.isInterface()) {
            return false;
        }

        // TODO
        return false;
    }

    public static boolean isFullConfigurationCandidate(AnnotationMetadata metadata) {
        return metadata.isAnnotated(Configuration.class.getName());
    }
}

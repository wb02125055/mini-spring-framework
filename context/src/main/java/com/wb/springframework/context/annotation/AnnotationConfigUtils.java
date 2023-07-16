package com.wb.springframework.context.annotation;

import com.wb.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import com.wb.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import com.wb.springframework.beans.factory.config.BeanDefinition;
import com.wb.springframework.beans.factory.config.BeanDefinitionHolder;
import com.wb.springframework.beans.factory.support.BeanDefinitionRegistry;
import com.wb.springframework.beans.factory.support.DefaultListableBeanFactory;
import com.wb.springframework.beans.factory.support.RootBeanDefinition;
import com.wb.springframework.context.support.GenericApplicationContext;
import com.wb.springframework.core.annotation.AnnotationAttributes;
import com.wb.springframework.core.type.AnnotatedTypeMetadata;
import com.wb.springframework.core.type.AnnotationMetadata;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author WangBing
 * @date 2023/5/20 23:25
 */
public abstract class AnnotationConfigUtils {

    public static final String CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME =
            "com.wb.springframework.context.annotation.internalConfigurationAnnotationProcessor";

    public static void registerAnnotationConfigProcessors(BeanDefinitionRegistry registry) {
        registerAnnotationConfigProcessors(registry, null);
    }

    public static Set<BeanDefinitionHolder> registerAnnotationConfigProcessors(BeanDefinitionRegistry registry, Object source) {
        DefaultListableBeanFactory beanFactory = unwrapDefaultListableBeanFactory(registry);
        final Set<BeanDefinitionHolder> beanDefs = new LinkedHashSet<>();

        if (!registry.containsBeanDefinition(CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME)) {
            RootBeanDefinition rbd = new RootBeanDefinition(ConfigurationClassPostProcessor.class);
            rbd.setSource(source);
            beanDefs.add(registerPostProcessor(registry, rbd, CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME));
        }
        return beanDefs;
    }

    private static BeanDefinitionHolder registerPostProcessor(BeanDefinitionRegistry registry,
                                                              RootBeanDefinition definition, String beanName) {
        // 表示这个bean是一个spring内部使用的bean
        definition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        // 注册bean定义
        registry.registerBeanDefinition(beanName, definition);
        return new BeanDefinitionHolder(beanName, definition);
    }

    public static void processCommonDefinitionAnnotations(AnnotatedBeanDefinition adb) {
        processCommonDefinitionAnnotations(adb, adb.getMetadata());
    }

    private static DefaultListableBeanFactory unwrapDefaultListableBeanFactory(BeanDefinitionRegistry registry) {
        if (registry instanceof GenericApplicationContext) {
            return ((GenericApplicationContext) registry).getDefaultListableBeanFactory();
        }
        return null;
    }

    static void processCommonDefinitionAnnotations(AnnotatedBeanDefinition adb, AnnotatedTypeMetadata metadata) {
    }

    public static Set<AnnotationAttributes> attributesForRepeatable(AnnotationMetadata metadata,
                                                                    Class<?> containerClass,
                                                                    Class<?> annotationClass) {
        return attributesForRepeatable(metadata, containerClass.getName(), annotationClass.getName());
    }

    @SuppressWarnings("unchecked")
    static Set<AnnotationAttributes> attributesForRepeatable(
            AnnotationMetadata metadata, String containerClassName, String annotationClassName) {
        Set<AnnotationAttributes> result = new LinkedHashSet<>();

        // Direct annotation present?
        addAttributesIfNotNull(result, metadata.getAnnotationAttributes(annotationClassName, false));

        Map<String, Object> container = metadata.getAnnotationAttributes(containerClassName, false);
        if (container != null && container.containsKey("value")) {
            for (Map<String, Object> containedAttributes : (Map<String, Object>[]) container.get("value")) {
                addAttributesIfNotNull(result, containedAttributes);
            }
        }

        // Return merged result
        return Collections.unmodifiableSet(result);
    }

    private static void addAttributesIfNotNull(
            Set<AnnotationAttributes> result, Map<String, Object> attributes
    ) {
        if (null != attributes) {
            result.add(AnnotationAttributes.fromMap(attributes));
        }
    }
















}

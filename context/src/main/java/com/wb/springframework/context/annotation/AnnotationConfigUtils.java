package com.wb.springframework.context.annotation;

import com.wb.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import com.wb.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import com.wb.springframework.beans.factory.config.BeanDefinition;
import com.wb.springframework.beans.factory.config.BeanDefinitionHolder;
import com.wb.springframework.beans.factory.support.BeanDefinitionRegistry;
import com.wb.springframework.beans.factory.support.DefaultListableBeanFactory;
import com.wb.springframework.beans.factory.support.RootBeanDefinition;
import com.wb.springframework.context.support.GenericApplicationContext;
import com.wb.springframework.core.type.AnnotatedTypeMetadata;

import java.util.LinkedHashSet;
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
}

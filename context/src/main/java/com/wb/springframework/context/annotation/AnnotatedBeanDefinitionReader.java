package com.wb.springframework.context.annotation;

import com.wb.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import com.wb.springframework.beans.factory.config.BeanDefinitionHolder;
import com.wb.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import com.wb.springframework.beans.factory.support.BeanDefinitionRegistry;
import com.wb.springframework.beans.factory.support.BeanNameGenerator;

import java.util.function.Supplier;

/**
 * @author WangBing
 * @date 2023/5/20 23:23
 */
public class AnnotatedBeanDefinitionReader {

    private final BeanDefinitionRegistry registry;

    private ScopeMetadataResolver scopeMetadataResolver = new AnnotationScopeMetadataResolver();

    private BeanNameGenerator beanNameGenerator = new AnnotationBeanNameGenerator();

    public AnnotatedBeanDefinitionReader(BeanDefinitionRegistry registry) {
        this.registry = registry;
        // 注册内部使用的后置处理器
        AnnotationConfigUtils.registerAnnotationConfigProcessors(this.registry);
    }

    public void register(Class<?>... componentClasses) {
        for (Class<?> componentClass : componentClasses) {
            registerBean(componentClass);
        }
    }

    public void registerBean(Class<?> componentClass) {
        doRegisterBean(componentClass, null, null);
    }

    <T> void doRegisterBean(Class<T> beanClass, Supplier<T> instanceSupplier, String name) {
        AnnotatedGenericBeanDefinition abd = new AnnotatedGenericBeanDefinition(beanClass);
        abd.setInstanceSupplier(instanceSupplier);
        ScopeMetadata scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(abd);
        abd.setScope(scopeMetadata.getScopeName());

        String beanName = null != name ? name : this.beanNameGenerator.generateBeanName(abd, this.registry);
        AnnotationConfigUtils.processCommonDefinitionAnnotations(abd);

        BeanDefinitionHolder beanDefinitionHolder = new BeanDefinitionHolder(beanName, abd);
        BeanDefinitionReaderUtils.registerBeanDefinition(beanDefinitionHolder, this.registry);
    }
}

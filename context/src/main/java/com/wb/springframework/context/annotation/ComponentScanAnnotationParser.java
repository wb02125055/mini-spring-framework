package com.wb.springframework.context.annotation;

import com.wb.springframework.beans.BeanUtils;
import com.wb.springframework.beans.factory.config.BeanDefinitionHolder;
import com.wb.springframework.beans.factory.support.BeanDefinitionRegistry;
import com.wb.springframework.beans.factory.support.BeanNameGenerator;
import com.wb.springframework.context.ConfigurableApplicationContext;
import com.wb.springframework.core.annotation.AnnotationAttributes;
import com.wb.springframework.core.io.ResourceLoader;
import com.wb.springframework.core.type.filter.AbstractTypeHierarchyTraversingFilter;
import com.wb.springframework.util.ClassUtils;
import com.wb.springframework.util.StringUtils;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author WangBing
 * @date 2023/7/16 17:57
 */
class ComponentScanAnnotationParser {

    private final ResourceLoader resourceLoader;

    private final BeanNameGenerator beanNameGenerator;

    private final BeanDefinitionRegistry registry;

    public ComponentScanAnnotationParser(ResourceLoader resourceLoader, BeanNameGenerator beanNameGenerator,
                                         BeanDefinitionRegistry registry) {
        this.resourceLoader = resourceLoader;
        this.beanNameGenerator = beanNameGenerator;
        this.registry = registry;
    }


    public Set<BeanDefinitionHolder> parse(AnnotationAttributes componentScan, final String declaringClass) {
        // 创建扫描器对象
        boolean useDefaultFilters = componentScan.getBoolean("useDefaultFilters");
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(this.registry,
                useDefaultFilters, this.resourceLoader);
        Class<? extends BeanNameGenerator> generatorClass = componentScan.getClass("nameGenerator");
        boolean useInheritedGenerator = (BeanNameGenerator.class == generatorClass);
        scanner.setBeanNameGenerator(useInheritedGenerator ? this.beanNameGenerator : BeanUtils.instantiateClass(generatorClass));

        // TODO: 其他属性的解析

        // 获取要扫描的包路径
        Set<String> basePackages = new LinkedHashSet<>();
        String[] basePackagesArray = componentScan.getStringArray("basePackages");
        for (String pkg : basePackagesArray) {
            String[] tokenized = StringUtils.tokenizeToStringArray(pkg, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
            Collections.addAll(basePackages, tokenized);
        }
        // TODO: 根据basePackageClasses来扫描

        if (basePackages.isEmpty()) {
            basePackages.add(ClassUtils.getPackageName(declaringClass));
        }

        scanner.addExcludeFilter(new AbstractTypeHierarchyTraversingFilter(false, false) {
            @Override
            protected boolean matchClassName(String className) {
                return declaringClass.equals(className);
            }
        });

        // 通过ClassPathBeanDefinitionScanner对象执行扫描操作
        return scanner.doScan(StringUtils.toStringArray(basePackages));
    }

















}

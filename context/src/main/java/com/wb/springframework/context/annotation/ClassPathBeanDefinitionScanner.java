package com.wb.springframework.context.annotation;

import com.wb.springframework.beans.factory.config.BeanDefinition;
import com.wb.springframework.beans.factory.config.BeanDefinitionHolder;
import com.wb.springframework.beans.factory.support.BeanDefinitionRegistry;
import com.wb.springframework.beans.factory.support.BeanNameGenerator;
import com.wb.springframework.core.io.ResourceLoader;
import com.wb.springframework.core.type.filter.TypeFilter;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author WangBing
 * @date 2023/7/16 18:11
 */
public class ClassPathBeanDefinitionScanner extends ClassPathScanningCandidateComponentProvider {

    private final BeanDefinitionRegistry registry;

    private final List<TypeFilter> excludeFilters = new LinkedList<>();

    private BeanNameGenerator beanNameGenerator = new AnnotationBeanNameGenerator();

    public ClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters,
                                          ResourceLoader resourceLoader) {
        this.registry = registry;
        if (useDefaultFilters) {
            registerDefaultFilters();
        }
        setResourceLoader(resourceLoader);
    }


    public void addExcludeFilter(TypeFilter excludeFilter) {
        this.excludeFilters.add(0, excludeFilter);
    }

    public void setBeanNameGenerator(BeanNameGenerator beanNameGenerator) {
        this.beanNameGenerator = null != beanNameGenerator ? beanNameGenerator : new AnnotationBeanNameGenerator();
    }

    /**
     * 解析bean定义
     *
     * @param basePackages 在配置类中指定的包路径
     * @return 解析完成的bean定义
     */
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitions = new LinkedHashSet<>();
        for (String basePackage : basePackages) {
            // 从类路径下扫描候选的bean定义
            Set<BeanDefinition> candidates = findCandidateComponents(basePackage);
            for (BeanDefinition candidate : candidates) {

            }
        }

        return beanDefinitions;
    }


}

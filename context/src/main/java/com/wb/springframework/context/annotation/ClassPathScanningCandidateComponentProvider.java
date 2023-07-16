package com.wb.springframework.context.annotation;

import com.wb.springframework.beans.factory.config.BeanDefinition;
import com.wb.springframework.context.ResourceLoaderAware;
import com.wb.springframework.context.index.CandidateComponentsIndex;
import com.wb.springframework.core.annotation.AnnotationUtils;
import com.wb.springframework.core.io.Resource;
import com.wb.springframework.core.io.ResourceLoader;
import com.wb.springframework.core.io.support.PathMatchingResourcePatternResolver;
import com.wb.springframework.core.io.support.ResourcePatternResolver;
import com.wb.springframework.core.type.filter.AnnotationTypeFilter;
import com.wb.springframework.core.type.filter.AssignableTypeFilter;
import com.wb.springframework.core.type.filter.TypeFilter;
import com.wb.springframework.stereotype.Component;
import com.wb.springframework.stereotype.Indexed;
import com.wb.springframework.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author WangBing
 * @date 2023/7/16 18:14
 */
public class ClassPathScanningCandidateComponentProvider implements ResourceLoaderAware {

    static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";

    private String resourcePattern = DEFAULT_RESOURCE_PATTERN;

    private final List<TypeFilter> includeFilters = new LinkedList<>();

    private CandidateComponentsIndex componentsIndex;

    private ResourcePatternResolver resourcePatternResolver;

    protected ClassPathScanningCandidateComponentProvider() {}

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {

    }

    protected void registerDefaultFilters() {
        this.includeFilters.add(new AnnotationTypeFilter(Component.class));
    }


    public Set<BeanDefinition> findCandidateComponents(String basePackage) {
        if (this.componentsIndex != null && indexSupportsIncludeFilters()) {
            return addCandidateComponentsFromIndex(this.componentsIndex, basePackage);
        }
        // 扫描包下面的候选组件
        return scanCandidateComponents(basePackage);
    }

    private Set<BeanDefinition> scanCandidateComponents(String basePackage) {
        Set<BeanDefinition> candidates = new LinkedHashSet<>();
        try {
            // classpath*: + com/wb/spring/propertyvalue/**/*.class
            // resourcePattern默认为: **/*.class
            String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + resolveBasePackage(basePackage) + '/' +
                    this.resourcePattern;
            Resource[] resources = getResourcePatternResolver().getResources(packageSearchPath);
            for (Resource resource : resources) {
                if (resource.isReadable()) {

                }
            }

        } catch (Exception e) {

        }
        return candidates;
    }

    private ResourcePatternResolver getResourcePatternResolver() {
        if (this.resourcePatternResolver == null) {
            this.resourcePatternResolver = new PathMatchingResourcePatternResolver();
        }
        return this.resourcePatternResolver;
    }

    protected String resolveBasePackage(String basePackage) {
        // 将类路径中的 . 替换为 /
        return ClassUtils.convertClassNameToResourcePath(basePackage);
    }

    private Set<BeanDefinition> addCandidateComponentsFromIndex(CandidateComponentsIndex index, String basePackage) {
        Set<BeanDefinition> candidates = new LinkedHashSet<>();
        // TODO
        System.out.println("addCandidateComponentsFromIndex还没有实现");
        return candidates;
    }

    private boolean indexSupportsIncludeFilters() {
        for (TypeFilter includeFilter : this.includeFilters) {
            if (!includeSupportsIncludeFilter(includeFilter)) {
                return false;
            }
        }
        return true;
    }

    private boolean includeSupportsIncludeFilter(TypeFilter filter) {
        if (filter instanceof AnnotationTypeFilter) {
            Class<? extends Annotation> annotation = ((AnnotationTypeFilter) filter).getAnnotationType();
            return AnnotationUtils.isAnnotationDeclaredLocally(Indexed.class, annotation) ||
                    annotation.getName().startsWith("javax.");
        }
        if (filter instanceof AssignableTypeFilter) {
            Class<?> target = ((AssignableTypeFilter) filter).getTargetType();
            return AnnotationUtils.isAnnotationDeclaredLocally(Indexed.class, target);
        }
        return false;
    }

}

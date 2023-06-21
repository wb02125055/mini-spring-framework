package com.wb.springframework.context.annotation;

import com.wb.springframework.core.io.DescriptiveResource;
import com.wb.springframework.core.io.Resource;
import com.wb.springframework.core.type.AnnotationMetadata;
import com.wb.springframework.core.type.StandardAnnotationMetadata;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author WangBing
 * @date 2023/6/20 07:50
 */
public final class ConfigurationClass {

    private final Set<ConfigurationClass> importedBy = new LinkedHashSet<>(1);

    private final AnnotationMetadata metadata;

    private final Resource resource;

    private String beanName;

    public ConfigurationClass(Class<?> clazz, String beanName) {
        this.metadata = new StandardAnnotationMetadata(clazz, true);
        this.resource = new DescriptiveResource(clazz.getName());
        this.beanName = beanName;
    }
    public boolean isImported() {
        return !this.importedBy.isEmpty();
    }


    public AnnotationMetadata getMetadata() {
        return metadata;
    }
}

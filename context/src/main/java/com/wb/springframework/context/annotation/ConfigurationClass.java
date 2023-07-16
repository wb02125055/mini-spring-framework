package com.wb.springframework.context.annotation;

import com.wb.springframework.core.io.DescriptiveResource;
import com.wb.springframework.core.io.Resource;
import com.wb.springframework.core.type.AnnotationMetadata;
import com.wb.springframework.core.type.StandardAnnotationMetadata;
import com.wb.springframework.core.type.classreading.MetadataReader;
import com.wb.springframework.util.ClassUtils;

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
    public ConfigurationClass(AnnotationMetadata metadata, String beanName) {
        this.metadata = metadata;
        this.resource = new DescriptiveResource(metadata.getClassName());
        this.beanName = beanName;
    }
    public ConfigurationClass(Class<?> clazz, ConfigurationClass importedBy) {
        this.metadata = new StandardAnnotationMetadata(clazz, true);
        this.resource = new DescriptiveResource(clazz.getName());
        this.importedBy.add(importedBy);
    }
    public ConfigurationClass(MetadataReader metadataReader, ConfigurationClass importedBy) {
        this.metadata = metadataReader.getAnnotationMetadata();
        this.resource = metadataReader.getResource();
        this.importedBy.add(importedBy);
    }
    public boolean isImported() {
        return !this.importedBy.isEmpty();
    }


    public AnnotationMetadata getMetadata() {
        return metadata;
    }

    public String getSimpleName() {
        return ClassUtils.getShortName(getMetadata().getClassName());
    }
}

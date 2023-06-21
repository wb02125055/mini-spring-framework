package com.wb.springframework.context.annotation;

import com.wb.springframework.beans.BeanDefinitionStoreException;
import com.wb.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import com.wb.springframework.beans.factory.config.BeanDefinition;
import com.wb.springframework.beans.factory.config.BeanDefinitionHolder;
import com.wb.springframework.beans.factory.support.AbstractBeanDefinition;
import com.wb.springframework.beans.factory.support.BeanDefinitionRegistry;
import com.wb.springframework.core.Ordered;
import com.wb.springframework.core.annotation.AnnotationUtils;
import com.wb.springframework.core.env.Environment;
import com.wb.springframework.core.io.ResourceLoader;
import com.wb.springframework.core.type.AnnotationMetadata;
import com.wb.springframework.core.type.StandardAnnotationMetadata;
import com.wb.springframework.core.type.classreading.MetadataReader;
import com.wb.springframework.core.type.classreading.MetadataReaderFactory;
import com.wb.springframework.util.ClassUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author WangBing
 * @date 2023/6/20 07:42
 */
class ConfigurationClassParser {

    private final Map<ConfigurationClass, ConfigurationClass> configurationClasses = new LinkedHashMap<>();

    private final Environment environment;

    private final ResourceLoader resourceLoader;

    private final BeanDefinitionRegistry registry;

    private final MetadataReaderFactory metadataReaderFactory;

    public ConfigurationClassParser(MetadataReaderFactory metadataReaderFactory,
                                    Environment environment,
                                    ResourceLoader resourceLoader,
                                    BeanDefinitionRegistry registry) {
        this.metadataReaderFactory = metadataReaderFactory;
        this.environment = environment;
        this.resourceLoader = resourceLoader;
        this.registry = registry;
    }

    public void parse(Set<BeanDefinitionHolder> configCandidates) {
        for (BeanDefinitionHolder holder : configCandidates) {
            BeanDefinition bd = holder.getBeanDefinition();
            try {
                if (bd instanceof AnnotatedBeanDefinition) {
                   // no op
                } else if (bd instanceof AbstractBeanDefinition && ((AbstractBeanDefinition) bd).hasBeanClass()) {
                    parse(((AbstractBeanDefinition) bd).getBeanClass(), holder.getBeanName());
                } else {
                    // no op
                }
            } catch (BeanDefinitionStoreException ex) {
                throw ex;
            } catch (Throwable e) {
                throw new BeanDefinitionStoreException("Failed to parse configuration class ["
                    + bd.getBeanClassName() + "]", e);
            }
        }
    }


    protected final void parse(Class<?> clazz, String beanName) throws IOException {
        processConfigurationClass(new ConfigurationClass(clazz, beanName));
    }

    protected void processConfigurationClass(ConfigurationClass configClass) throws IOException {
        // todo : condition evaluator
        ConfigurationClass existingClass = this.configurationClasses.get(configClass);
        if (existingClass != null) {
            // todo: imported
            if (configClass.isImported()) {
                // merge
                return ;
            } else {
                this.configurationClasses.remove(configClass);
                // todo
            }
        }

        SourceClass sourceClass = asSourceClass(configClass);
    }

    private SourceClass asSourceClass(ConfigurationClass configurationClass) throws IOException {
        AnnotationMetadata metadata = configurationClass.getMetadata();
        if (metadata instanceof StandardAnnotationMetadata) {
            return asSourceClass(((StandardAnnotationMetadata) metadata).getIntrospectedClass());
        }
        return asSourceClass(metadata.getClassName());
    }

    SourceClass asSourceClass(Class<?> classType) throws IOException {
        if (classType == null) {
            return new SourceClass(Object.class);
        }
        try {
            for (Annotation ann : classType.getAnnotations()) {
                AnnotationUtils.validateAnnotation(ann);
            }
            return new SourceClass(classType);
        } catch (Throwable ex) {
            return asSourceClass(classType.getName());
        }
    }

    SourceClass asSourceClass(String className) throws IOException {
        if (className == null) {
            return new SourceClass(Object.class);
        }
        if (className.startsWith("java")) {
            try {
                return new SourceClass(ClassUtils.forName(className, this.resourceLoader.getClassLoader()));
            } catch (ClassNotFoundException ex) {
                throw new IOException("Failed to load class [" + className + "]", ex);
            }
        }
        return new SourceClass(this.metadataReaderFactory.getMetadataReader(className));
    }


    private class SourceClass implements Ordered {
        private final Object source;
        private final AnnotationMetadata metadata;
        public SourceClass(Object source) {
            this.source = source;
            if (source instanceof Class) {
                this.metadata = new StandardAnnotationMetadata((Class<?>) source, true);
            } else {
                this.metadata = ((MetadataReader) source).getAnnotationMetadata();
            }
        }

        @Override
        public int getOrder() {
            return 0;
        }
    }













}

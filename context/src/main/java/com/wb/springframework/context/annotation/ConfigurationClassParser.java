package com.wb.springframework.context.annotation;

import com.wb.springframework.beans.factory.support.BeanDefinitionRegistry;
import com.wb.springframework.core.env.Environment;
import com.wb.springframework.core.io.ResourceLoader;
import com.wb.springframework.core.type.classreading.MetadataReaderFactory;

/**
 * @author WangBing
 * @date 2023/6/20 07:42
 */
class ConfigurationClassParser {

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

}

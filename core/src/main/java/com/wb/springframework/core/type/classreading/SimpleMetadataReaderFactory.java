package com.wb.springframework.core.type.classreading;

import com.wb.springframework.core.io.DefaultResourceLoader;
import com.wb.springframework.core.io.Resource;
import com.wb.springframework.core.io.ResourceLoader;

import java.io.IOException;

/**
 * @author WangBing
 * @date 2023/7/22 16:48
 */
public class SimpleMetadataReaderFactory implements MetadataReaderFactory {

    private final ResourceLoader resourceLoader;

    public SimpleMetadataReaderFactory() {
        this.resourceLoader = new DefaultResourceLoader();
    }

    public SimpleMetadataReaderFactory(ResourceLoader resourceLoader) {
        this.resourceLoader = null != resourceLoader ? resourceLoader : new DefaultResourceLoader();
    }

    @Override
    public MetadataReader getMetadataReader(String className) throws IOException {
        return null;
    }

    @Override
    public MetadataReader getMetadataReader(Resource resource) throws IOException {
        return new SimpleMetadataReader(resource, this.resourceLoader.getClassLoader());
    }
}

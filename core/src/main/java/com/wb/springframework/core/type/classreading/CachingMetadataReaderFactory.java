package com.wb.springframework.core.type.classreading;

import com.wb.springframework.core.io.DefaultResourceLoader;
import com.wb.springframework.core.io.Resource;
import com.wb.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author WangBing
 * @date 2023/6/16 23:21
 */
public class CachingMetadataReaderFactory extends SimpleMetadataReaderFactory {

    public static final int DEFAULT_CACHE_LIMIT = 256;

    private Map<Resource, MetadataReader> metadataReaderCache;

    public CachingMetadataReaderFactory() {
        super();
        setCacheLimit(DEFAULT_CACHE_LIMIT);
    }

    public CachingMetadataReaderFactory(ResourceLoader resourceLoader) {
        super(resourceLoader);
        if (resourceLoader instanceof DefaultResourceLoader) {
            this.metadataReaderCache = ((DefaultResourceLoader) resourceLoader).getResourceCache(MetadataReader.class);
        } else {
            setCacheLimit(DEFAULT_CACHE_LIMIT);
        }
    }

    public void setCacheLimit(int cacheLimit) {
        if (cacheLimit < 0) {
            this.metadataReaderCache = null;
        } else if (this.metadataReaderCache instanceof LocalResourceCache) {
            ((LocalResourceCache) this.metadataReaderCache).setCacheLimit(cacheLimit);
        } else {
            this.metadataReaderCache = new LocalResourceCache(cacheLimit);
        }
    }


    @Override
    public MetadataReader getMetadataReader(String className) throws IOException {
        return null;
    }


    private static class LocalResourceCache extends LinkedHashMap<Resource, MetadataReader> {
        private volatile int cacheLimit;
        public LocalResourceCache(int cacheLimit) {
            super(cacheLimit, 0.75f, true);
            this.cacheLimit = cacheLimit;
        }

        public void setCacheLimit(int cacheLimit) {
            this.cacheLimit = cacheLimit;
        }

        public int getCacheLimit() {
            return cacheLimit;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<Resource, MetadataReader> eldest) {
            return size() > this.cacheLimit;
        }
    }


















}

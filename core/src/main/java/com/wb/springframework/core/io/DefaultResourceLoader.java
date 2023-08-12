package com.wb.springframework.core.io;

import com.wb.springframework.util.ClassUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author WangBing
 * @date 2023/6/20 07:48
 */
public class DefaultResourceLoader implements ResourceLoader {

    private ClassLoader classLoader;

    private final Map<Class<?>, Map<Resource, ?>> resourceCaches = new ConcurrentHashMap<>(4);

    public DefaultResourceLoader() {
        this.classLoader = ClassUtils.getDefaultClassLoader();
    }

    @Override
    public ClassLoader getClassLoader() {
        return null;
    }

    @Override
    public Resource getResource(String location) {
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T> Map<Resource, T> getResourceCache(Class<T> valueType) {
        return (Map<Resource, T>) this.resourceCaches.computeIfAbsent(valueType, key -> new ConcurrentHashMap<>());
    }

}

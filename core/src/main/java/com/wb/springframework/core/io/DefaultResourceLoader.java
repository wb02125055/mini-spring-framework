package com.wb.springframework.core.io;

import com.wb.springframework.util.ClassUtils;

/**
 * @author WangBing
 * @date 2023/6/20 07:48
 */
public class DefaultResourceLoader implements ResourceLoader {

    private ClassLoader classLoader;

    public DefaultResourceLoader() {
        this.classLoader = ClassUtils.getDefaultClassLoader();
    }

    @Override
    public ClassLoader getClassLoader() {
        return null;
    }
}

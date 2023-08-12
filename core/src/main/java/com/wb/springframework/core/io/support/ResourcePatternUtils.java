package com.wb.springframework.core.io.support;

import com.wb.springframework.core.io.ResourceLoader;

/**
 * @author WangBing
 * @date 2023/7/16 22:44
 */
public abstract class ResourcePatternUtils {

    public static ResourcePatternResolver getResourcePatternResolver(ResourceLoader resourceLoader) {
        // AnnotationConfigApplicationContext实现自ResourcePatternResolver接口
        if (resourceLoader instanceof ResourcePatternResolver) {
            return (ResourcePatternResolver) resourceLoader;
        } else if (resourceLoader != null) {
            return new PathMatchingResourcePatternResolver(resourceLoader);
        } else {
            return new PathMatchingResourcePatternResolver();
        }
    }
}

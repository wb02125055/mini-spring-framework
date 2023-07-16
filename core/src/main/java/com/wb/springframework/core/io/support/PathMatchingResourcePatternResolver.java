package com.wb.springframework.core.io.support;

import com.wb.springframework.core.io.Resource;

import java.io.IOException;

/**
 * @author WangBing
 * @date 2023/7/16 19:56
 */
public class PathMatchingResourcePatternResolver implements ResourcePatternResolver {
    @Override
    public ClassLoader getClassLoader() {
        return null;
    }

    @Override
    public Resource[] getResources(String locationPattern) throws IOException {
        return new Resource[0];
    }
}

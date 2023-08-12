package com.wb.springframework.core.io;

/**
 * @author WangBing
 * @date 2023/6/20 07:47
 */
public interface ResourceLoader {

    ClassLoader getClassLoader();

    Resource getResource(String location);
}

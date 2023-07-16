package com.wb.springframework.core.io.support;

import com.wb.springframework.core.io.Resource;
import com.wb.springframework.core.io.ResourceLoader;

import java.io.IOException;

/**
 * @author WangBing
 * @date 2023/7/16 19:47
 */
public interface ResourcePatternResolver extends ResourceLoader {

    String CLASSPATH_ALL_URL_PREFIX = "classpath*:";

    Resource[] getResources(String locationPattern) throws IOException;

}

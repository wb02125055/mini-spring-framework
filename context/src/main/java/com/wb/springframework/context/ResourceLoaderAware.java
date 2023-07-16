package com.wb.springframework.context;

import com.wb.springframework.beans.factory.Aware;
import com.wb.springframework.core.io.ResourceLoader;

/**
 * @author WangBing
 * @date 2023/7/16 18:19
 * 通过XXXAware接口中的setXXX方法来给容器中注入对应的XXX实例
 */
public interface ResourceLoaderAware extends Aware {

    void setResourceLoader(ResourceLoader resourceLoader);
}

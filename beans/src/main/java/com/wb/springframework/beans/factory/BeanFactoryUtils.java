package com.wb.springframework.beans.factory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author WangBing
 * @date 2023/5/27 17:27
 */
public abstract class BeanFactoryUtils {

    private static final Map<String, String> transformedBeanNameCache = new ConcurrentHashMap<>();

    public static final String GENERATED_BEAN_NAME_SEPARATOR = "#";

    public static String transformedBeanName(String name) {
        if (!name.startsWith(BeanFactory.FACTORY_BEAN_PREFIX)) {
            return name;
        }
        return transformedBeanNameCache.computeIfAbsent(name, beanName -> {
            do {
                beanName = beanName.substring(BeanFactory.FACTORY_BEAN_PREFIX.length());
            } while (beanName.startsWith(BeanFactory.FACTORY_BEAN_PREFIX));
            return beanName;
        });
    }

    public static boolean isFactoryDereference(String name) {
        return name != null && name.startsWith(BeanFactory.FACTORY_BEAN_PREFIX);
    }
}

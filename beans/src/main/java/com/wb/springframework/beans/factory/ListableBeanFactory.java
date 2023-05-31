package com.wb.springframework.beans.factory;

/**
 * @author WangBing
 * @date 2023/5/20 14:36
 */
public interface ListableBeanFactory extends BeanFactory {
    boolean containsBeanDefinition(String beanName);

    String[] getBeanNamesForType(Class<?> type, boolean includeNonSingletons, boolean allowEagerInit);
}

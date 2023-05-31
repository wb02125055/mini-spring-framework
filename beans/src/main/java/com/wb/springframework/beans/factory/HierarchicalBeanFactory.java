package com.wb.springframework.beans.factory;

/**
 * @author WangBing
 * @date 2023/5/21 21:36
 */
public interface HierarchicalBeanFactory extends BeanFactory {
    boolean containsLocalBean(String beanName);
}

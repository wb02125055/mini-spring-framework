package com.wb.springframework.beans.factory.support;

import com.wb.springframework.beans.BeansException;
import com.wb.springframework.beans.factory.BeanFactory;

/**
 * @author WangBing
 * @date 2023/5/18 22:23
 */
public interface InstantiationStrategy {
    Object instantiate(RootBeanDefinition mbd, String beanName, BeanFactory owner) throws BeansException;
}

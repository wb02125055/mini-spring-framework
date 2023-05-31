package com.wb.springframework.beans.factory.support;

import com.wb.springframework.beans.BeanInstantiationException;
import com.wb.springframework.beans.BeanUtils;
import com.wb.springframework.beans.factory.BeanFactory;

import java.lang.reflect.Constructor;

/**
 * @author WangBing
 * @date 2023/5/28 18:02
 */
public class SimpleInstantiationStrategy implements InstantiationStrategy {
    @Override
    public Object instantiate(RootBeanDefinition mbd, String beanName, BeanFactory owner) {
        Class<?> beanClass = mbd.getBeanClass();
        if (beanClass.isInterface()) {
            throw new BeanInstantiationException("Specified class is an interface");
        }
        Constructor<?> constructorToUse;
        try {
            constructorToUse = beanClass.getDeclaredConstructor();
        } catch (Throwable e) {
            throw new BeanInstantiationException("No default constructor found when instantiate bean with name '" + beanName + "'", e);
        }
        return BeanUtils.instantiateClass(constructorToUse);
    }
}

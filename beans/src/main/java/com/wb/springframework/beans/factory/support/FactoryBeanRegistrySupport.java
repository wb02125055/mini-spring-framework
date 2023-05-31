package com.wb.springframework.beans.factory.support;

import com.wb.springframework.beans.BeansException;
import com.wb.springframework.beans.factory.BeanCreationException;
import com.wb.springframework.beans.factory.FactoryBean;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author WangBing
 * @date 2023/5/27 21:43
 */
public abstract class FactoryBeanRegistrySupport extends DefaultSingletonBeanRegistry {

    private final Map<String, Object> factoryBeanObjectCache = new ConcurrentHashMap<>(16);

    protected Class<?> getTypeForFactoryBean(final FactoryBean<?> factoryBean) {
        try {
            return factoryBean.getObjectType();
        } catch (Throwable ex) {
            // log for error
            return null;
        }
    }

    protected Object getObjectFromFactoryBean(FactoryBean<?> factory, String beanName, boolean shouldPostProcess) {
        if (factory.isSingleton() && containsSingleton(beanName)) {
            // TODO
            return null;
        } else {
            Object object = doGetObjectFromFactoryBean(factory, beanName);
            if (shouldPostProcess) {
                try {
                    object = postProcessObjectFromFactoryBean(object, beanName);
                } catch (Throwable e) {
                    throw new BeanCreationException("Bean '" + beanName + " post processing of FactoryBean's object failed.", e);
                }
            }
            return object;
        }
    }

    private Object doGetObjectFromFactoryBean(final FactoryBean<?> factory, final String beanName)
        throws BeanCreationException {
        Object object;
        try {
            object = factory.getObject();
        } catch (Throwable ex) {
            throw new BeanCreationException("FactoryBean threw exception on object creation", ex);
        }
        if (object == null) {
            object = new NullBean();
        }
        return object;
    }

    protected Object postProcessObjectFromFactoryBean(Object object, String beanName) throws BeansException {
        return object;
    }

    protected Object getCachedObjectForFactoryBean(String beanName) {
        return this.factoryBeanObjectCache.get(beanName);
    }






}

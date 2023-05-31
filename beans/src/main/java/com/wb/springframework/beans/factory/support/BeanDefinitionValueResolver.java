package com.wb.springframework.beans.factory.support;

import com.wb.springframework.beans.BeanDefinitionStoreException;
import com.wb.springframework.beans.BeansException;
import com.wb.springframework.beans.PropertyValue;
import com.wb.springframework.beans.TypeConverter;
import com.wb.springframework.beans.factory.BeanCreationException;
import com.wb.springframework.beans.factory.BeanFactoryUtils;
import com.wb.springframework.beans.factory.config.BeanDefinition;
import com.wb.springframework.beans.factory.config.BeanDefinitionHolder;
import com.wb.springframework.beans.factory.config.RuntimeBeanNameReference;
import com.wb.springframework.beans.factory.config.RuntimeBeanReference;

/**
 * @author WangBing
 * @date 2023/5/28 22:02
 */
public class BeanDefinitionValueResolver {

    private final AbstractBeanFactory beanFactory;

    private final String beanName;

    private final BeanDefinition beanDefinition;

    private final TypeConverter typeConverter;

    public BeanDefinitionValueResolver(AbstractBeanFactory beanFactory, String beanName,
                                       BeanDefinition beanDefinition, TypeConverter typeConverter) {

        this.beanFactory = beanFactory;
        this.beanName = beanName;
        this.beanDefinition = beanDefinition;
        this.typeConverter = typeConverter;
    }

    public Object resolveValueIfNecessary(Object argName, Object value) {
        if (value instanceof RuntimeBeanReference) {
            RuntimeBeanReference ref = (RuntimeBeanReference) value;
            return resolveReference(argName, ref);
        }
        else if (value instanceof RuntimeBeanNameReference) {
            String refName = ((RuntimeBeanNameReference) value).getBeanName();
            refName = String.valueOf(doEvaluate(refName));
            if (!this.beanFactory.containsBean(refName)) {
                throw new BeanDefinitionStoreException("Invalid bean name '" + refName + "' in bean reference for " + argName);
            }
            return refName;
        }
        else if (value instanceof BeanDefinitionHolder) {
            BeanDefinitionHolder bdHolder = (BeanDefinitionHolder) value;
            return resolveInnerBean(argName, bdHolder.getBeanName(), bdHolder.getBeanDefinition());
        }
        // TODO
        return null;
    }

    private Object resolveInnerBean(Object argName, String innerBeanName, BeanDefinition innerBd) {
        RootBeanDefinition mbd = null;
        try {
            mbd = this.beanFactory.getMergedBeanDefinition(innerBeanName, innerBd, this.beanDefinition);
            String actualInnerBeanName = innerBeanName;
            if (mbd.isSingleton()) {
                actualInnerBeanName = adaptInnerBeanName(innerBeanName);
            }
            this.beanFactory.registerContainedBean(actualInnerBeanName, this.beanName);
            // TODO
            return null;
        } catch (BeansException ex) {
            throw new BeanCreationException("Cannot create inner bean '" + innerBeanName + "' "
                + (mbd != null && mbd.getBeanClassName() != null ? " of type " + mbd.getBeanClassName() : "")
                + "while setting " + argName, ex);
        }
    }

    private String adaptInnerBeanName(String innerBeanName) {
        String actualInnerBeanName = innerBeanName;
        int counter = 0;
        while (this.beanFactory.isBeanNameInUse(actualInnerBeanName)) {
            counter++;
            actualInnerBeanName = innerBeanName + BeanFactoryUtils.GENERATED_BEAN_NAME_SEPARATOR + counter;
        }
        return actualInnerBeanName;
    }

    private Object resolveReference(Object argName, RuntimeBeanReference ref) {
        try {
            String refName = ref.getBeanName();
            refName = String.valueOf(doEvaluate(refName));
            Object bean = this.beanFactory.getBean(refName);
            this.beanFactory.registerDependentBean(refName, this.beanName);
            if (bean instanceof NullBean) {
                bean = null;
            }
            return bean;
        } catch (BeansException ex) {
            throw new BeanCreationException("Cannot resolve reference to bean '" + ref.getBeanName() + "' while setting " + argName, ex);
        }
    }

    private Object doEvaluate(String value) {
        return this.beanFactory.evaluateBeanDefinitionString(value, this.beanDefinition);
    }

    public AbstractBeanFactory getBeanFactory() {
        return beanFactory;
    }

    public String getBeanName() {
        return beanName;
    }

    public BeanDefinition getBeanDefinition() {
        return beanDefinition;
    }

    public TypeConverter getTypeConverter() {
        return typeConverter;
    }
}

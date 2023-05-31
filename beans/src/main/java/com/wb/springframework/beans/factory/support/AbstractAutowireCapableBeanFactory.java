package com.wb.springframework.beans.factory.support;

import com.wb.springframework.beans.*;
import com.wb.springframework.beans.factory.BeanCreationException;
import com.wb.springframework.beans.factory.BeanFactory;
import com.wb.springframework.beans.factory.config.AutowireCapableBeanFactory;
import com.wb.springframework.beans.factory.config.BeanDefinition;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author WangBing
 * @date 2023/5/21 17:45
 */
public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory
        implements AutowireCapableBeanFactory {

    private InstantiationStrategy instantiationStrategy = new CglibSubclassingInstantiationStrategy();

    @Override
    protected Object createBean(String beanName, RootBeanDefinition mbd, Object[] args) throws BeanCreationException {
        RootBeanDefinition mbdToUse = mbd;
        Class<?> resolvedClass = resolveBeanClass(mbd, beanName);
        if (resolvedClass != null && !mbd.hasBeanClass() && mbd.getBeanClassName() != null) {
            mbdToUse = new RootBeanDefinition(mbd);
            mbdToUse.setBeanClass(resolvedClass);
        }
        try {
            return doCreateBean(beanName, mbdToUse, args);
        } catch (BeanCreationException e) {
            throw e;
        } catch (Throwable ex) {
            throw new BeanCreationException("Unexpected exception during bean creation", ex);
        }
    }

    protected Object doCreateBean(final String beanName, final RootBeanDefinition mbd, final Object[] args)
        throws BeanCreationException {
        BeanWrapper instanceWrapper = createBeanInstance(beanName, mbd, args);
        final Object bean = instanceWrapper.getWrappedInstance();
        Class<?> beanType = instanceWrapper.getWrappedClass();
        if (beanType != NullBean.class) {
            mbd.resolvedTargetType = beanType;
        }
        boolean earlySingletonExposure = mbd.isSingleton()
                && isSingletonCurrentInCreation(beanName);
        if (earlySingletonExposure) {
            addSingletonFactory(beanName, () -> getEarlyBeanReference(beanName, mbd, bean));
        }
        Object exposedObject = bean;
        try {
            populateBean(beanName, mbd, instanceWrapper);

            exposedObject = initializeBean(beanName, exposedObject, mbd);
        } catch (Throwable e) {
            throw new BeanCreationException("Initialization of bean named '" + beanName + "' failed", e);
        }
        return exposedObject;
    }

    protected Object initializeBean(final String beanName, final Object bean, RootBeanDefinition mbd) {
        // TODO init...
        return bean;
    }

    protected void populateBean(String beanName, RootBeanDefinition mbd, BeanWrapper bw) {
        if (bw == null) {
            if (mbd.hasPropertyValues()) {
                throw new BeanCreationException("Cannot apply property values to null instance named '" + beanName + "'");
            }
            return;
        }
        PropertyValues pvs = mbd.hasPropertyValues() ? mbd.getPropertyValues() : null;
        if (pvs != null) {
            applyPropertyValues(beanName, mbd, bw, pvs);
        }
    }

    protected void applyPropertyValues(String beanName, BeanDefinition mbd, BeanWrapper bw, PropertyValues pvs) {
        if (pvs.isEmpty()) {
            return;
        }
        MutablePropertyValues mpvs = null;
        List<PropertyValue> original;
        if (pvs instanceof MutablePropertyValues) {
            mpvs = (MutablePropertyValues) pvs;
            if (mpvs.isConverted()) {
                try {
                    bw.setPropertyValues(mpvs);
                    return;
                } catch (BeansException ex) {
                    throw new BeanCreationException("Error setting property values for bean named '" + beanName + "'", ex);
                }
            }
            original = mpvs.getPropertyValueList();
        } else {
            original = Arrays.asList(pvs.getPropertyValues());
        }
        TypeConverter converter = getCustomTypeConverter();
        if (converter == null) {
            converter = bw;
        }
        BeanDefinitionValueResolver valueResolver = new BeanDefinitionValueResolver(this, beanName, mbd, converter);
        List<PropertyValue> deepCopy = new ArrayList<>(original.size());
        boolean resolveNecessary = false;
        for (PropertyValue pv : original) {
            if (pv.isConverted()) {
                deepCopy.add(pv);
            } else {
                String propertyName = pv.getName();
                Object originalValue = pv.getValue();
                Object resolvedValue = valueResolver.resolveValueIfNecessary(pv, originalValue);

            }
        }
    }

    protected Object getEarlyBeanReference(String beanName, RootBeanDefinition mbd, Object bean) {
        return bean;
    }

    protected BeanWrapper createBeanInstance(String beanName, RootBeanDefinition mbd, Object[] args) {
        Class<?> beanClass = resolveBeanClass(mbd, beanName);
        if (beanClass != null && !Modifier.isPublic(beanClass.getModifiers()) && !mbd.isNonPublicAccessAllowed()) {
            throw new BeanCreationException("Bean class isn't public, and non-publish access not allowed: " + beanClass.getName());
        }
        Supplier<?> instanceSupplier = mbd.getInstanceSupplier();
        if (instanceSupplier != null) {
            return obtainFromSupplier(instanceSupplier, beanName);
        }
        // TODO 对于原型bean的获取
        Constructor<?>[] constructors = determineConstructorsFromBeanPostProcessors(beanClass, beanName);
        if (constructors != null) {
            return autowireConstructor(beanName, mbd, constructors, args);
        }
        return instantiateBean(beanName, mbd);
    }

    protected BeanWrapper instantiateBean(final String beanName, final RootBeanDefinition mbd) {
        try {
            final BeanFactory parent = this;
            Object beanInstance = getInstantiationStrategy().instantiate(mbd, beanName, parent);
            BeanWrapper bw = new BeanWrapperImpl(beanInstance);
            initBeanWrapper(bw);
            return bw;
        } catch (Throwable ex) {
            throw new BeanCreationException("Instantiation of bean named '" + beanName + "' failed", ex);
        }
    }

    protected BeanWrapper autowireConstructor(String beanName, RootBeanDefinition mbd,
                                              Constructor<?>[] ctors, Object[] explicitArgs) {
        return null;
    }

    protected Constructor<?>[] determineConstructorsFromBeanPostProcessors(Class<?> beanClass, String beanName)
        throws BeansException  {
        // TODO
        return null;
    }

    protected BeanWrapper obtainFromSupplier(Supplier<?> instanceSupplier, String beanName) {
        Object instance = instanceSupplier.get();
        if (instance == null) {
            instance = new NullBean();
        }
        BeanWrapper bw = new BeanWrapperImpl(instance);
        initBeanWrapper(bw);
        return bw;
    }


    public InstantiationStrategy getInstantiationStrategy() {
        return instantiationStrategy;
    }

    public void setInstantiationStrategy(InstantiationStrategy instantiationStrategy) {
        this.instantiationStrategy = instantiationStrategy;
    }
}

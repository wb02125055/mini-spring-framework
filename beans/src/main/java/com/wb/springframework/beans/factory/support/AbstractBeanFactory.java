package com.wb.springframework.beans.factory.support;

import com.wb.springframework.beans.*;
import com.wb.springframework.beans.factory.*;
import com.wb.springframework.beans.factory.config.*;
import com.wb.springframework.core.NamedThreadLocal;
import com.wb.springframework.core.ResolvableType;
import com.wb.springframework.util.ClassUtils;
import com.wb.springframework.util.ObjectUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author WangBing
 * @date 2023/5/18 22:21
 */
public abstract class AbstractBeanFactory extends FactoryBeanRegistrySupport implements ConfigurableBeanFactory {

    private final Map<String, RootBeanDefinition> mergedBeanDefinitions = new ConcurrentHashMap<>(256);

    private BeanExpressionResolver beanExpressionResolver;

    private final Map<String, Scope> scopes = new ConcurrentHashMap<>(8);

    private final Set<String> alreadyCreated = Collections.newSetFromMap(new ConcurrentHashMap<>(256));

    private final ThreadLocal<Object> prototypesCurrentlyInCreation =
            new NamedThreadLocal<>("Prototype beans currently in creation");

    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();

    private TypeConverter typeConverter;

    public ClassLoader getBeanClassLoader() {
        return beanClassLoader;
    }

    public void setBeanClassLoader(ClassLoader beanClassLoader) {
        this.beanClassLoader = null != beanClassLoader ? beanClassLoader : ClassUtils.getDefaultClassLoader();
    }

    protected boolean isFactoryBean(String beanName, RootBeanDefinition mbd) {
        Class<?> beanType = predictBeanType(beanName, mbd, FactoryBean.class);
        return null != beanType && FactoryBean.class.isAssignableFrom(beanType);
    }

    protected Class<?> predictBeanType(String beanName, RootBeanDefinition mbd, Class<?>... typesToMatch) {
        Class<?> targetType = mbd.getTargetType();
        if (targetType != null) {
            return targetType;
        }
        if (mbd.getFactoryMethodName() != null) {
            return null;
        }
        return resolveBeanClass(mbd, beanName, typesToMatch);
    }

    protected RootBeanDefinition getMergedLocalBeanDefinition(String beanName) throws BeansException {
        RootBeanDefinition mbd = this.mergedBeanDefinitions.get(beanName);
        if (mbd != null) {
            return mbd;
        }
        return getMergedBeanDefinition(beanName, getBeanDefinition(beanName));
    }

    public boolean isBeanNameInUse(String beanName) {
        return isAlias(beanName) || containsLocalBean(beanName) || hasDependentBean(beanName);
    }

    protected RootBeanDefinition getMergedBeanDefinition(String beanName, BeanDefinition bd)
        throws BeanDefinitionStoreException {
        return getMergedBeanDefinition(beanName, bd, null);
    }


    protected RootBeanDefinition getMergedBeanDefinition(
            String beanName, BeanDefinition bd,BeanDefinition containingBd)
            throws BeanDefinitionStoreException {
        synchronized (this.mergedBeanDefinitions) {
            RootBeanDefinition mbd = null;
            if (containingBd == null) {
                mbd = this.mergedBeanDefinitions.get(beanName);
            }
            if (mbd == null) {
                if (bd.getParentName() == null) {
                    if (bd instanceof RootBeanDefinition) {
                        mbd = ((RootBeanDefinition) bd).cloneBeanDefinition();
                    } else {
                        mbd = new RootBeanDefinition(bd);
                    }
                } else {
                    BeanDefinition pbd;
                    try {
                        String parentBeanName = transformedBeanName(bd.getParentName());
                    } catch (Exception e) {

                    }
                }
            }
            return mbd;
        }
    }

    protected String transformedBeanName(String name) {
        return canonicalName(BeanFactoryUtils.transformedBeanName(name));
    }

    protected void initBeanWrapper(BeanWrapper bw) {
        // set conversionService
        // register customEditors
    }

    protected Class<?> resolveBeanClass(final RootBeanDefinition mbd, String beanName,
                                        final Class<?>... typesToMatch) {
        try {
            if (mbd.hasBeanClass()) {
                return mbd.getBeanClass();
            }
            return doResolveBeanClass(mbd, typesToMatch);
        } catch (Exception e) {
            throw new CannotLoadBeanClassException("resolve beanClass error for beanName '" + beanName + "'", e);
        }
    }

    private Class<?> doResolveBeanClass(RootBeanDefinition mbd, Class<?>... typesToMatch)
        throws ClassNotFoundException {
        ClassLoader beanClassLoader = getBeanClassLoader();
        boolean freshResolve = false;
        if (!ObjectUtils.isEmpty(typesToMatch)) {
            // todo
        }
        String className = mbd.getBeanClassName();
        if (className != null) {
            Object evaluated = evaluateBeanDefinitionString(className, mbd);
            if (!className.equals(evaluated)) {
                if (evaluated instanceof Class) {
                    return (Class<?>) evaluated;
                } else if (evaluated instanceof String) {
                    className = (String) evaluated;
                    freshResolve = true;
                } else {
                    throw new IllegalStateException("Invalid class name expression result: " + evaluated);
                }
            }
            if (freshResolve) {
                if (beanClassLoader != null) {
                    try {
                        return beanClassLoader.loadClass(className);
                    } catch (ClassNotFoundException ex) {
                        // log
                    }
                }
                return ClassUtils.forName(className, beanClassLoader);
            }
        }
        // caching
        return mbd.resolveBeanClass(beanClassLoader);
    }

    protected Object evaluateBeanDefinitionString(String value, BeanDefinition beanDefinition) {
        if (this.beanExpressionResolver == null) {
            return value;
        }
        Scope scope = null;
        if (beanDefinition != null) {
            String scopeName = beanDefinition.getScope();
            if (scopeName != null) {
                scope = getRegisteredScope(scopeName);
            }
        }
        return this.beanExpressionResolver.evaluate(value, new BeanExpressionContext(this, scope));
    }

    @Override
    public boolean isFactoryBean(String name) throws NoSuchBeanDefinitionException {
        String beanName = transformedBeanName(name);
        Object beanInstance = getSingleton(beanName, false);
        if (beanInstance != null) {
            return (beanInstance instanceof FactoryBean);
        }
        return isFactoryBean(beanName, getMergedLocalBeanDefinition(beanName));
    }

    @Override
    public Scope getRegisteredScope(String scopeName) {
        return this.scopes.get(scopeName);
    }

    @Override
    public void registerScope(String scopeName, Scope scope) {
        if (SCOPE_SINGLETON.equals(scopeName) || SCOPE_PROTOTYPE.equals(scopeName)) {
            throw new IllegalArgumentException("Cannot replace existing scopes 'singleton' and 'prototype'");
        }
        Scope previous = this.scopes.put(scopeName, scope);
        if (previous != null && previous != scope) {
            // todo log info: scope was replaced
        } else {
            // todo log info: register new scope
        }
    }

    @Override
    public boolean containsBean(String beanName) {
        return false;
    }

    @Override
    public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        String beanName = transformedBeanName(name);
        Object beanInstance = getSingleton(beanName, false);
        if (beanInstance != null) {
            if (beanInstance instanceof FactoryBean) {
                return BeanFactoryUtils.isFactoryDereference(name) || ((FactoryBean<?>) beanInstance).isSingleton();
            } else {
                return BeanFactoryUtils.isFactoryDereference(name);
            }
        }
        RootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName);
        if (mbd.isSingleton()) {
            if (isFactoryBean(beanName, mbd)) {
                if (BeanFactoryUtils.isFactoryDereference(name)) {
                    return true;
                }
                FactoryBean<?> factoryBean = (FactoryBean<?>) getBean(FACTORY_BEAN_PREFIX + beanName);
                return factoryBean.isSingleton();
            } else {
                return !BeanFactoryUtils.isFactoryDereference(name);
            }
        }
        return false;
    }

    @Override
    public boolean isTypeMatch(String name, Class<?> typeToMatch) throws NoSuchBeanDefinitionException {
        return isTypeMatch(name, ResolvableType.forRawClass(typeToMatch));
    }

    @Override
    public boolean isTypeMatch(String name, ResolvableType matchType) throws NoSuchBeanDefinitionException {
        String beanName = transformedBeanName(name);

        Object beanInstance = getSingleton(beanName, false);
        if (beanInstance != null && beanInstance.getClass() != NullBean.class) {
            if (beanInstance instanceof FactoryBean) {
                if (!BeanFactoryUtils.isFactoryDereference(name)) {
                    Class<?> type = getTypeForFactoryBean((FactoryBean<?>) beanInstance);
                    return type != null && matchType.isAssignableFrom(type);
                }
                return matchType.isInstance(beanInstance);
            } else if (!BeanFactoryUtils.isFactoryDereference(name)) {
                if (matchType.isInstance(beanInstance)) {
                    return true;
                } else if (matchType.hasGenerics() && containsBeanDefinition(beanName)) {
                    RootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName);
                    Class<?> targetType = mbd.getTargetType();
                    if (targetType != null && targetType != ClassUtils.getUserClass(beanInstance)) {
                        Class<?> classToMatch = matchType.resolve();
                        if (classToMatch != null && !classToMatch.isInstance(beanInstance)) {
                            return false;
                        }
                        if (matchType.isAssignableFrom(targetType)) {
                            return true;
                        }
                    }
                    ResolvableType resolvableType = mbd.targetType;
                    if (resolvableType == null) {
                        resolvableType = mbd.factoryMethodReturnType;
                    }
                    return resolvableType != null && matchType.isAssignableFrom(resolvableType);
                }
            }
            return false;
        } else if (containsSingleton(beanName) && !containsBeanDefinition(beanName)) {
            return false;
        }

        RootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName);
        Class<?> classToMatch = matchType.resolve();
        if (classToMatch == null) {
            classToMatch = FactoryBean.class;
        }
        Class<?>[] typesToMatch = FactoryBean.class == classToMatch ? new Class<?>[] { classToMatch } :
                new Class<?>[] {FactoryBean.class, classToMatch};
        Class<?> beanType = predictBeanType(beanName, mbd, typesToMatch);
        if (beanType == null) {
            return false;
        }
        if (FactoryBean.class.isAssignableFrom(beanType)) {
            if (!BeanFactoryUtils.isFactoryDereference(name) && beanInstance == null) {
                beanType = getTypeForFactoryBean(beanName, mbd);
                if (beanType == null) {
                    return false;
                }
            }
        } else if (BeanFactoryUtils.isFactoryDereference(name)) {
            // Special case: A SmartInstantiationAwareBeanPostProcessor returned a non-FactoryBean
            // type but we nevertheless are being asked to dereference a FactoryBean...
            // Let's check the original bean class and proceed with it if it is a FactoryBean.
            beanType = predictBeanType(beanName, mbd, FactoryBean.class);
            if (beanType == null || !FactoryBean.class.isAssignableFrom(beanType)) {
                return false;
            }
        }
        ResolvableType resolvableType = mbd.targetType;
        if (resolvableType == null) {
            resolvableType = mbd.factoryMethodReturnType;
        }
        if (resolvableType != null && resolvableType.resolve() == beanType) {
            return matchType.isAssignableFrom(resolvableType);
        }
        return matchType.isAssignableFrom(beanType);
    }

    protected TypeConverter getCustomTypeConverter() {
        return this.typeConverter;
    }

    public void setTypeConverter(TypeConverter typeConverter) {
        this.typeConverter = typeConverter;
    }

    protected Class<?> getTypeForFactoryBean(String beanName, RootBeanDefinition mbd) {
        if (!mbd.isSingleton()) {
            return null;
        }
        try {
            FactoryBean<?> factoryBean = doGetBean(FACTORY_BEAN_PREFIX + beanName, FactoryBean.class, null, true);
            return getTypeForFactoryBean(factoryBean);
        } catch (BeanCreationException e) {
            // log
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    protected <T> T doGetBean(final String name, final Class<T> requiredType,
                              final Object[] args, boolean typeCheckOnly) throws BeansException {
        final String beanName = transformedBeanName(name);
        Object bean;
        Object sharedInstance = getSingleton(beanName);
        if (sharedInstance != null && args == null) {
            bean = getObjectForBeanInstance(sharedInstance, name, beanName, null);
        } else {
            if (!typeCheckOnly) {
                markBeanAsCreated(beanName);
            }
            try {
                final RootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName);
                checkMergedBeanDefinition(mbd, beanName, args);
                if (mbd.isSingleton()) {
                    // 如果bean定义是单实例的
                    sharedInstance = getSingleton(beanName, () -> {
                        try {
                            return createBean(beanName, mbd, args);
                        } catch (BeansException e) {
                            destroySingleton(beanName);
                            throw e;
                        }
                    });
                    bean = getObjectForBeanInstance(sharedInstance, name, beanName, mbd);
                } else if (mbd.isPrototype()) {
                    // 如果bean定义是多实例的
                    bean = getObjectForBeanInstance(sharedInstance, name, beanName, mbd);
                } else {
                    // 获取scope的名称
                    String scopeName = mbd.getScope();
                    // 获取scope
                    final Scope scope = this.scopes.get(scopeName);
                    if (scope == null) {
                        throw new IllegalStateException("No Scope registered for scope name '" + scopeName + "'");
                    }
                    try {
                        // 根据指定的scope来创建bean实例
                        Object scopedInstance = scope.get(beanName, () -> {
                            // 在初始化之前进行一一些准备工作，将bean的名称添加到prototypeCurrentlyInCreation
                            beforePrototypeCreation(beanName);
                            try {
                                // 创建bean实例
                                return createBean(beanName, mbd, args);
                            } finally {
                                // 从prototypeCurrentlyInCreation中移除
                                afterPrototypeCreation(beanName);
                            }
                        });
                        bean = getObjectForBeanInstance(scopedInstance, name, beanName, mbd);
                    } catch (IllegalStateException ex) {
                        throw new BeanCreationException("Scope '" + scopeName
                                + "' is not active for the current thread; consider defining a scoped proxy for this bean"
                                + " if you intend to refer to it from a singleton",
                                ex);
                    }
                }
            } catch (Exception e) {
                throw e;
            }
        }
        return (T) bean;
    }


    @SuppressWarnings("unchecked")
    protected void afterPrototypeCreation(String beanName) {
        Object curValue = this.prototypesCurrentlyInCreation.get();
        if (curValue instanceof String) {
            this.prototypesCurrentlyInCreation.remove();
        } else if (curValue instanceof Set) {
            Set<String> beanNameSet = (Set<String>) curValue;
            beanNameSet.remove(beanName);
            if (beanNameSet.isEmpty()) {
                this.prototypesCurrentlyInCreation.remove();
            }
        }
    }
    @SuppressWarnings("unchecked")
    protected void beforePrototypeCreation(String beanName) {
        Object curValue = this.prototypesCurrentlyInCreation.get();
        if (curValue == null) {
            // 如果当前bean没有在创建，则将当前要创建的bean的名称设置到线程本地变量中
            this.prototypesCurrentlyInCreation.set(beanName);
        } else if (curValue instanceof String) {
            Set<String> beanNameSet = new HashSet<>(2);
            beanNameSet.add((String) curValue);
            beanNameSet.add(beanName);
            this.prototypesCurrentlyInCreation.set(beanNameSet);
        } else {
            Set<String> beanNameSet = (Set<String>) curValue;
            beanNameSet.add(beanName);
        }
    }

    protected void checkMergedBeanDefinition(RootBeanDefinition mbd, String beanName, Object[] args)
        throws BeanDefinitionStoreException {
        if (mbd.isAbstract()) {
            throw new BeanIsAbstractException("Bean definition named '" + beanName + "' is abstract");
        }
    }

    protected void markBeanAsCreated(String beanName) {
        if (!this.alreadyCreated.contains(beanName)) {
            synchronized (this.mergedBeanDefinitions) {
                if (!this.alreadyCreated.contains(beanName)) {
                    clearMergedBeanDefinition(beanName);
                    this.alreadyCreated.add(beanName);
                }
            }
        }
    }

    protected void clearMergedBeanDefinition(String beanName) {
        this.mergedBeanDefinitions.remove(beanName);
    }

    protected Object getObjectForBeanInstance(Object beanInstance, String name, String beanName, RootBeanDefinition mbd) {
        if (BeanFactoryUtils.isFactoryDereference(name)) {
            if (beanInstance instanceof NullBean) {
                return beanInstance;
            }
            if (!(beanInstance instanceof FactoryBean)) {
                throw new BeanIsNotAFactoryBeanException(beanName, beanInstance.getClass());
            }
        }
        if (!(beanInstance instanceof FactoryBean) || BeanFactoryUtils.isFactoryDereference(name)) {
            return beanInstance;
        }
        Object object = null;
        if (mbd == null) {
            object = getCachedObjectForFactoryBean(beanName);
        }
        if (object == null) {
            FactoryBean<?> factory = (FactoryBean<?>) beanInstance;
            if (mbd == null && containsBeanDefinition(beanName)) {
                mbd = getMergedLocalBeanDefinition(beanName);
            }
            boolean synthetic = mbd != null && mbd.isSynthetic();
            object = getObjectFromFactoryBean(factory, beanName, !synthetic);
        }
        return object;
    }

    @Override
    public boolean containsLocalBean(String name) {
        String beanName = transformedBeanName(name);
        return (containsSingleton(beanName) || containsBeanDefinition(beanName)) &&
                (!BeanFactoryUtils.isFactoryDereference(beanName) || isFactoryBean(beanName));
    }

    protected abstract BeanDefinition getBeanDefinition(String beanName) throws BeansException;

    protected abstract boolean containsBeanDefinition(String beanName);

    protected abstract Object createBean(String beanName, RootBeanDefinition mbd, Object[] args) throws BeanCreationException;
}

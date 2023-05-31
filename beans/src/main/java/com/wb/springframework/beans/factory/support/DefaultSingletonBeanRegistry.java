package com.wb.springframework.beans.factory.support;

import com.wb.springframework.beans.factory.BeanCreationException;
import com.wb.springframework.beans.factory.BeanCurrentlyInCreationException;
import com.wb.springframework.beans.factory.ObjectFactory;
import com.wb.springframework.core.SimpleAliasRegistry;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author WangBing
 * @date 2023/5/21 17:49
 */
public class DefaultSingletonBeanRegistry extends SimpleAliasRegistry implements SingletonBeanRegistry {

    /**
     * 一级缓存
     */
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);
    /**
     * 二级缓存
     */
    private final Map<String, Object> earlySingletonObjects = new HashMap<>(16);
    /**
     * 三级缓存
     */
    private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap<>(16);


    private final Set<String> registeredSingletons = new LinkedHashSet<>(256);

    private final Map<String, Set<String>> dependentBeanMap = new ConcurrentHashMap<>(64);

    private final Map<String, Set<String>> containedBeanMap = new ConcurrentHashMap<>(16);

    private final Map<String, Set<String>> dependenciesForBeanMap = new ConcurrentHashMap<>(64);

    private final Set<String> singletonsCurrentlyInCreation =
            Collections.newSetFromMap(new ConcurrentHashMap<>(16));

    private final Set<String> inCreationCheckExclusions =
            Collections.newSetFromMap(new ConcurrentHashMap<>(16));


    @Override
    public void registerSingleton(String beanName, Object singletonObject) {
        synchronized (this.singletonObjects) {
            Object oldObject = this.singletonObjects.get(beanName);
            if (oldObject != null) {
                throw new IllegalStateException("Could not register object '" + singletonObject +
                        "' under bean name '" + beanName + "' this is already object '" + oldObject + "' bound");
            }
            addSingleton(beanName, singletonObject);
        }
    }

    protected void addSingleton(String beanName, Object singletonObject) {
        synchronized (this.singletonObjects) {
            this.singletonObjects.put(beanName, singletonObject);

            this.singletonFactories.remove(beanName);

            this.earlySingletonObjects.remove(beanName);

            this.registeredSingletons.add(beanName);
        }
    }

    @Override
    public Object getSingleton(String beanName) {
        return getSingleton(beanName, true);
    }

    protected Object getSingleton(String beanName, boolean allowEarlyReference) {
        Object singletonObject = this.singletonObjects.get(beanName);
        if (singletonObject == null && isSingletonCurrentInCreation(beanName)) {
            synchronized (this.singletonObjects) {
                singletonObject = this.earlySingletonObjects.get(beanName);
                if (singletonObject == null && allowEarlyReference) {
                    ObjectFactory<?> singletonFactory = this.singletonFactories.get(beanName);
                    if (singletonFactory != null) {
                        singletonObject = singletonFactory.getObject();
                        this.earlySingletonObjects.put(beanName, singletonObject);
                        this.singletonFactories.remove(beanName);
                    }
                }
            }
        }
        return singletonObject;
    }

    public Object getSingleton(String beanName, ObjectFactory<?> singletonFactory) {
        synchronized (this.singletonObjects) {
            Object singletonObject = this.singletonObjects.get(beanName);
            if (singletonObject == null) {
                beforeSingletonCreation(beanName);
                boolean newSingleton = false;
                try {
                    singletonObject = singletonFactory.getObject();
                    newSingleton = true;
                } catch (IllegalStateException ex) {
                    singletonObject = this.singletonObjects.get(beanName);
                    if (singletonObject == null) {
                        throw ex;
                    }
                } catch (BeanCreationException ex) {
                    throw ex;
                } finally {
                    afterSingletonCreation(beanName);
                    if (newSingleton) {
                        addSingleton(beanName, singletonObject);
                    }
                }
            }
            return singletonObject;
        }
    }

    public void destroySingleton(String beanName) {
        removeSingleton(beanName);
    }

    protected void addSingletonFactory(String beanName, ObjectFactory<?> singletonFactory) {
        synchronized (this.singletonObjects) {
            if (!this.singletonObjects.containsKey(beanName)) {
                this.singletonFactories.put(beanName, singletonFactory);

                this.earlySingletonObjects.remove(beanName);

                this.registeredSingletons.add(beanName);
            }
        }
    }

    protected void removeSingleton(String beanName) {
        synchronized (this.singletonObjects) {
            this.singletonObjects.remove(beanName);
            this.singletonFactories.remove(beanName);
            this.earlySingletonObjects.remove(beanName);
            this.registeredSingletons.remove(beanName);
        }
    }

    protected void beforeSingletonCreation(String beanName) {
        if (!this.inCreationCheckExclusions.contains(beanName) && !this.singletonsCurrentlyInCreation.add(beanName)) {
            throw new BeanCurrentlyInCreationException("Bean '" + beanName + "' is current in creation: Is there an unresolvable circular reference?");
        }
    }


    protected void afterSingletonCreation(String beanName) {
        if (!this.inCreationCheckExclusions.contains(beanName) && !this.singletonsCurrentlyInCreation.remove(beanName)) {
            throw new IllegalStateException("Singleton '" + beanName + "' is not currently in creation.");
        }
    }

    public void registerDependentBean(String beanName, String dependentBeanName) {
        String canonicalName = canonicalName(beanName);
        synchronized (this.dependentBeanMap) {
            Set<String> dependentBeans = this.dependentBeanMap.computeIfAbsent(canonicalName, k -> new LinkedHashSet<>(8));
            if (!dependentBeans.add(dependentBeanName)) {
                return;
            }
            synchronized (this.dependenciesForBeanMap) {
                Set<String> dependenciesForBean =
                        this.dependenciesForBeanMap.computeIfAbsent(dependentBeanName, k -> new LinkedHashSet<>(8));
                dependenciesForBean.add(canonicalName);
            }
        }
    }



    protected boolean hasDependentBean(String beanName) {
        return this.dependentBeanMap.containsKey(beanName);
    }

    public void registerContainedBean(String containedBeanName, String containingBeanName) {
        synchronized (this.containedBeanMap) {
            Set<String> containedBeans =
                    this.containedBeanMap.computeIfAbsent(containingBeanName, k -> new LinkedHashSet<>(8));
            if (!containedBeans.add(containedBeanName)) {
                return;
            }
        }
        registerDependentBean(containedBeanName, containingBeanName);
    }


    public boolean isSingletonCurrentInCreation(String beanName) {
        return this.singletonsCurrentlyInCreation.contains(beanName);
    }

    @Override
    public boolean containsSingleton(String beanName) {
        return this.singletonObjects.containsKey(beanName);
    }
}

package com.wb.springframework.beans.factory.support;

import com.wb.springframework.beans.BeanDefinitionStoreException;
import com.wb.springframework.beans.BeansException;
import com.wb.springframework.beans.NoSuchBeanDefinitionException;
import com.wb.springframework.beans.factory.BeanFactory;
import com.wb.springframework.beans.factory.CannotLoadBeanClassException;
import com.wb.springframework.beans.factory.config.BeanDefinition;
import com.wb.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import com.wb.springframework.core.ResolvableType;
import com.wb.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author WangBing
 * @date 2023/5/21 17:44
 */
public class DefaultListableBeanFactory extends AbstractAutowireCapableBeanFactory
        implements ConfigurableListableBeanFactory, BeanDefinitionRegistry {

    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);

    private final List<String> beanDefinitionNames = new ArrayList<>();

    private volatile Set<String> manualSingletonNames = new LinkedHashSet<>(16);

    private volatile String[] frozenBeanDefinitionNames;

    private Comparator<Object> dependencyComparator;

    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) throws BeanDefinitionStoreException {
        this.beanDefinitionMap.put(beanName, beanDefinition);
        this.beanDefinitionNames.add(beanName);
    }

    @Override
    public BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
        BeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);
        if (null == beanDefinition) {
            throw new NoSuchBeanDefinitionException(beanName);
        }
        return beanDefinition;
    }

    @Override
    public String[] getBeanDefinitionNames() {
        String[] frozenNames = this.frozenBeanDefinitionNames;
        if (frozenNames != null) {
            return frozenNames.clone();
        }
        return StringUtils.toStringArray(this.beanDefinitionNames);
    }

    @Override
    public boolean containsBeanDefinition(String beanName) {
        return this.beanDefinitionMap.containsKey(beanName);
    }

    @Override
    public String[] getBeanNamesForType(Class<?> type, boolean includeNonSingletons, boolean allowEagerInit) {
        return doGetBeanNamesForType(ResolvableType.forRawClass(type), includeNonSingletons, allowEagerInit);
    }

    private String[] doGetBeanNamesForType(ResolvableType type, boolean includeNoSingletons, boolean allowEagerInit) {
        List<String> result = new ArrayList<>();
        for (String beanName : this.beanDefinitionNames) {
            if (!isAlias(beanName)) {
                try {
                    RootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName);
                    if (!mbd.isAbstract() && (allowEagerInit || (mbd.hasBeanClass() || !mbd.isLazyInit()))) {
                        boolean isFactoryBean = isFactoryBean(beanName, mbd);
                        boolean matchFound = (allowEagerInit || !isFactoryBean || containsBeanDefinition(beanName)) &&
                                (includeNoSingletons || isSingleton(beanName)) &&
                                isTypeMatch(beanName, type);

                        if (!matchFound && isFactoryBean) {
                            beanName = BeanFactory.FACTORY_BEAN_PREFIX + beanName;
                            matchFound = (includeNoSingletons || mbd.isSingleton()) && isTypeMatch(beanName, type);
                        }
                        if (matchFound) {
                            result.add(beanName);
                        }
                    }
                } catch (CannotLoadBeanClassException | BeanDefinitionStoreException ex) {
                    if (allowEagerInit) {
                        throw ex;
                    }
                }
            }
        }
        for (String beanName : this.manualSingletonNames) {
            try {
                if (isFactoryBean(beanName)) {
                    if ((includeNoSingletons || isSingleton(beanName)) && isTypeMatch(beanName, type)) {
                        result.add(beanName);
                        continue;
                    }
                    beanName = BeanFactory.FACTORY_BEAN_PREFIX + beanName;
                }
                if (isTypeMatch(beanName, type)) {
                    result.add(beanName);
                }
            } catch (NoSuchBeanDefinitionException ex) {
                // todo no op
            }
        }
        return StringUtils.toStringArray(result);
    }

    @Override
    public Object getBean(String name) throws BeansException {
        return null;
    }

    @Override
    public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
        return doGetBean(name, requiredType, null, false);
    }

    @Override
    public Object getBean(String name, Object... args) throws BeansException {
        return null;
    }

    public Comparator<Object> getDependencyComparator() {
        return dependencyComparator;
    }

    public void setDependencyComparator(Comparator<Object> dependencyComparator) {
        this.dependencyComparator = dependencyComparator;
    }
}

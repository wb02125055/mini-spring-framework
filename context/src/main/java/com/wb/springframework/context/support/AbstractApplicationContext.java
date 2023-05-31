package com.wb.springframework.context.support;

import com.wb.springframework.beans.BeansException;
import com.wb.springframework.beans.NoSuchBeanDefinitionException;
import com.wb.springframework.beans.factory.config.BeanFactoryPostProcessor;
import com.wb.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import com.wb.springframework.context.ConfigurableApplicationContext;
import com.wb.springframework.core.ResolvableType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author WangBing
 * @date 2023/5/20 14:40
 */
public abstract class AbstractApplicationContext implements ConfigurableApplicationContext {

    private final Object startupShutdownMonitor = new Object();

    private final AtomicBoolean close = new AtomicBoolean();

    private final AtomicBoolean active = new AtomicBoolean();

    private final List<BeanFactoryPostProcessor> beanFactoryPostProcessors = new ArrayList<>();

    @Override
    public void refresh() throws BeansException, IllegalStateException {
        synchronized (startupShutdownMonitor) {
            // 准备刷新容器
            prepareRefresh();

            // 获取beanFactory
            ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();

            // 准备beanFactory
            prepareBeanFactory(beanFactory);

            try {
                // 留给开发者的一个扩展点
                postProcessBeanFactory(beanFactory);

                // 执行BeanFactory的后置处理器
                invokeBeanFactoryPostProcessors(beanFactory);

            } catch (BeansException ex) {
                throw ex;
            } finally {
                resetCommonCaches();
            }

        }
    }

    protected void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory) {
        PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(beanFactory, getBeanFactoryPostProcessors());
    }

    protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {

    }

    protected void resetCommonCaches() {
        // 重置缓存
    }

    protected void prepareBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        // 添加一些通用的后置处理器
    }

    protected ConfigurableListableBeanFactory obtainFreshBeanFactory() {
        refreshBeanFactory();
        return getBeanFactory();
    }

    protected void prepareRefresh() {
        this.active.set(true);
        this.close.set(false);
    }

    @Override
    public Object getBean(String name) throws BeansException {
        return null;
    }

    @Override
    public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
        return null;
    }

    @Override
    public boolean containsBean(String beanName) {
        return getBeanFactory().containsBean(beanName);
    }

    @Override
    public Object getBean(String name, Object... args) throws BeansException {
        return null;
    }

    @Override
    public String[] getAliases(String name) {
        return new String[0];
    }

    @Override
    public boolean containsBeanDefinition(String beanName) {
        return getBeanFactory().containsBeanDefinition(beanName);
    }

    @Override
    public boolean isTypeMatch(String name, Class<?> typeToMatch) throws NoSuchBeanDefinitionException {
        return getBeanFactory().isTypeMatch(name, typeToMatch);
    }

    @Override
    public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        return getBeanFactory().isSingleton(name);
    }

    @Override
    public boolean isTypeMatch(String name, ResolvableType matchType) throws NoSuchBeanDefinitionException {
        return getBeanFactory().isTypeMatch(name, matchType);
    }

    @Override
    public String[] getBeanNamesForType(Class<?> type, boolean includeNonSingletons, boolean allowEagerInit) {
        return getBeanFactory().getBeanNamesForType(type, includeNonSingletons, allowEagerInit);
    }

    public List<BeanFactoryPostProcessor> getBeanFactoryPostProcessors() {
        return beanFactoryPostProcessors;
    }

    protected abstract ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException;

    protected abstract void refreshBeanFactory() throws BeansException, IllegalStateException;
}

package com.wb.springframework.context.support;

import com.wb.springframework.beans.BeanDefinitionStoreException;
import com.wb.springframework.beans.BeansException;
import com.wb.springframework.beans.NoSuchBeanDefinitionException;
import com.wb.springframework.beans.factory.config.BeanDefinition;
import com.wb.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import com.wb.springframework.beans.factory.support.BeanDefinitionRegistry;
import com.wb.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author WangBing
 * @date 2023/5/20 14:39
 */
public class GenericApplicationContext extends AbstractApplicationContext implements BeanDefinitionRegistry {

    private final AtomicBoolean refreshed = new AtomicBoolean();

    private final DefaultListableBeanFactory beanFactory;

    public GenericApplicationContext() {
        this.beanFactory = new DefaultListableBeanFactory();
    }

    public GenericApplicationContext(DefaultListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) throws BeanDefinitionStoreException {
        this.beanFactory.registerBeanDefinition(beanName, beanDefinition);
    }

    @Override
    protected ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException {
        return this.beanFactory;
    }

    @Override
    protected void refreshBeanFactory() throws BeansException, IllegalStateException {
        if (!this.refreshed.compareAndSet(false, true)) {
            throw new IllegalStateException("GenericApplicationContext does't support ");
        }
    }

    public final DefaultListableBeanFactory getDefaultListableBeanFactory() {
        return this.beanFactory;
    }

    @Override
    public BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
        return this.beanFactory.getBeanDefinition(beanName);
    }

    @Override
    public boolean isBeanNameInUse(String beanName) {
        return this.beanFactory.isBeanNameInUse(beanName);
    }

    @Override
    public void registerAlias(String name, String alias) {

    }

    @Override
    public void removeAlias(String alias) {

    }

    @Override
    public boolean isAlias(String name) {
        return false;
    }
}

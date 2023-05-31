package com.wb.springframework.beans.factory.config;

import com.wb.springframework.beans.BeanMetadataElement;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author WangBing
 * @date 2023/5/21 17:24
 */
public class BeanDefinitionHolder implements BeanMetadataElement {

    private final String beanName;
    private final BeanDefinition beanDefinition;
    private final String[] aliases;

    public BeanDefinitionHolder(String beanName, BeanDefinition beanDefinition) {
        this(beanName, beanDefinition, null);
    }

    public BeanDefinitionHolder(String beanName, BeanDefinition beanDefinition, String[] aliases) {
        this.beanName = beanName;
        this.beanDefinition = beanDefinition;
        this.aliases = aliases;
    }

    public BeanDefinitionHolder(BeanDefinitionHolder holder) {
        this.beanName = holder.getBeanName();
        this.beanDefinition = holder.getBeanDefinition();
        this.aliases = holder.getAliases();
    }

    public String getBeanName() {
        return beanName;
    }

    public BeanDefinition getBeanDefinition() {
        return beanDefinition;
    }

    public String[] getAliases() {
        return aliases;
    }

    @Override
    public Object getSource() {
        return this.beanDefinition.getSource();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BeanDefinitionHolder that = (BeanDefinitionHolder) o;
        return Objects.equals(beanName, that.beanName) && Objects.equals(beanDefinition, that.beanDefinition) && Arrays.equals(aliases, that.aliases);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(beanName, beanDefinition);
        result = 31 * result + Arrays.hashCode(aliases);
        return result;
    }
}

package com.wb.springframework.beans;

/**
 * @author WangBing
 * @date 2023/5/20 23:40
 */
public class NoSuchBeanDefinitionException extends BeansException {

    private final String beanName;
    public NoSuchBeanDefinitionException(String name) {
        super("No bean named '" + name + "' available.");
        this.beanName = name;
    }

    public String getBeanName() {
        return beanName;
    }
}

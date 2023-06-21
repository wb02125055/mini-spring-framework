package com.wb.springframework.beans;

/**
 * @author WangBing
 * @date 2023/5/20 23:39
 */
public class BeanDefinitionStoreException extends BeansException {
    public BeanDefinitionStoreException(String msg) {
        super(msg);
    }
    public BeanDefinitionStoreException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

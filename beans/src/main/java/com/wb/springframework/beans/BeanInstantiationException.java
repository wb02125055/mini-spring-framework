package com.wb.springframework.beans;

/**
 * @author WangBing
 * @date 2023/5/28 18:14
 */
public class BeanInstantiationException extends BeansException {
    public BeanInstantiationException(String msg) {
        super(msg);
    }
    public BeanInstantiationException(String msg, Throwable e) {
        super(msg, e);
    }
}

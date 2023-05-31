package com.wb.springframework.beans.factory;

import com.wb.springframework.beans.BeansException;

/**
 * @author WangBing
 * @date 2023/5/27 22:10
 */
public class BeanCreationException extends BeansException {
    public BeanCreationException(String msg) {
        super(msg);
    }
    public BeanCreationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

package com.wb.springframework.beans.factory;

import com.wb.springframework.beans.BeansException;

/**
 * @author WangBing
 * @date 2023/5/27 13:09
 */
public class CannotLoadBeanClassException extends BeansException {
    public CannotLoadBeanClassException(String msg) {
        super(msg);
    }
    public CannotLoadBeanClassException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

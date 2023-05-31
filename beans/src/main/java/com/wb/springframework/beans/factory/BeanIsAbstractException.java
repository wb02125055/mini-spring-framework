package com.wb.springframework.beans.factory;

/**
 * @author WangBing
 * @date 2023/5/28 11:55
 */
public class BeanIsAbstractException extends BeanCreationException {
    public BeanIsAbstractException(String msg) {
        super(msg);
    }
}

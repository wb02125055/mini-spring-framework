package com.wb.springframework.beans.factory;

/**
 * @author WangBing
 * @date 2023/5/28 12:09
 */
public class BeanCurrentlyInCreationException extends BeanCreationException {
    public BeanCurrentlyInCreationException(String msg) {
        super(msg);
    }
}

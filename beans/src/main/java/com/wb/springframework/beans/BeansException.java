package com.wb.springframework.beans;

/**
 * @author WangBing
 * @date 2023/5/20 14:33
 */
public class BeansException extends RuntimeException {
    public BeansException(String msg) {
        super(msg);
    }
    public BeansException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

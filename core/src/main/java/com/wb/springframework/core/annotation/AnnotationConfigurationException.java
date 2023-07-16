package com.wb.springframework.core.annotation;

/**
 * @author WangBing
 * @date 2023/7/1 21:43
 */
public class AnnotationConfigurationException extends RuntimeException {
    public AnnotationConfigurationException(String message) {
        super(message);
    }

    public AnnotationConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}

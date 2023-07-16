package com.wb.springframework.core.annotation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author WangBing
 * @date 2023/7/10 22:21
 */
public class SynthesizedAnnotationInvocationHandler implements InvocationHandler {
    public SynthesizedAnnotationInvocationHandler(DefaultAnnotationAttributeExtractor attributeExtractor) {

    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return null;
    }
}

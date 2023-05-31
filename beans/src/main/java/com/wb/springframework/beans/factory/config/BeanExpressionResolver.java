package com.wb.springframework.beans.factory.config;

import com.wb.springframework.beans.BeansException;

/**
 * @author WangBing
 * @date 2023/5/27 13:27
 */
public interface BeanExpressionResolver {

    Object evaluate(String value, BeanExpressionContext evalContext) throws BeansException;
}

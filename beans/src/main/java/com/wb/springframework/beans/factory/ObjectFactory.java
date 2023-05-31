package com.wb.springframework.beans.factory;

import com.wb.springframework.beans.BeansException;

/**
 * @author WangBing
 * @date 2023/5/27 21:53
 */
@FunctionalInterface
public interface ObjectFactory<T> {

    T getObject() throws BeansException;
}

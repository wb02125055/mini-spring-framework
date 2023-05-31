package com.wb.springframework.beans;

/**
 * @author WangBing
 * @date 2023/5/28 13:56
 */
public class BeanWrapperImpl extends AbstractNestablePropertyAccessor implements BeanWrapper {

    public BeanWrapperImpl(Object object) {
        super(object);
    }
}

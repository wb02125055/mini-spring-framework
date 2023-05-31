package com.wb.springframework.beans.factory.support;

/**
 * @author WangBing
 * @date 2023/5/18 22:22
 */
public final class NullBean {

    NullBean() {}

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj == null;
    }

    @Override
    public int hashCode() {
        return NullBean.class.hashCode();
    }

    @Override
    public String toString() {
        return "null";
    }
}

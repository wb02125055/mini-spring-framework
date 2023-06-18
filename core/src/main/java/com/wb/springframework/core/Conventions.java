package com.wb.springframework.core;

/**
 * @author WangBing
 * @date 2023/6/16 23:11
 */
public final class Conventions {

    public static String getQualifiedAttributeName(Class<?> enclosingClass, String attributeName) {
        return enclosingClass.getName() + "." + attributeName;
    }
}

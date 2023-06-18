package com.wb.springframework.core.type;

/**
 * @author WangBing
 * @date 2023/6/17 14:55
 */
public class StandardClassMetadata implements ClassMetadata {

    private final Class<?> introspectedClass;

    public StandardClassMetadata(Class<?> introspectedClass) {
        this.introspectedClass = introspectedClass;
    }

    public final Class<?> getIntrospectedClass() {
        return introspectedClass;
    }

    @Override
    public String getClassName() {
        return this.introspectedClass.getName();
    }

    @Override
    public boolean isInterface() {
        return this.introspectedClass.isInterface();
    }
}

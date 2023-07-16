package com.wb.springframework.core.type.filter;

import com.wb.springframework.core.type.classreading.MetadataReader;
import com.wb.springframework.core.type.classreading.MetadataReaderFactory;

import java.io.IOException;

/**
 * @author WangBing
 * @date 2023/7/16 18:43
 */
public abstract class AbstractTypeHierarchyTraversingFilter implements TypeFilter {

    private final boolean considerInherited;

    private final boolean considerInterfaces;

    protected AbstractTypeHierarchyTraversingFilter(boolean considerInherited, boolean considerInterfaces) {
        this.considerInherited = considerInherited;
        this.considerInterfaces = considerInterfaces;
    }

    @Override
    public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
        System.out.println("TypeFilter中的match方法没实现，默认返回false......");
        return false;
    }

    protected boolean matchClassName(String className) {
        return false;
    }
}

package com.wb.springframework.core.type.filter;

import com.wb.springframework.core.type.classreading.MetadataReader;
import com.wb.springframework.core.type.classreading.MetadataReaderFactory;

import java.io.IOException;

/**
 * @author WangBing
 * @date 2023/7/16 18:40
 */
@FunctionalInterface
public interface TypeFilter {
    boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory)
            throws IOException;
}

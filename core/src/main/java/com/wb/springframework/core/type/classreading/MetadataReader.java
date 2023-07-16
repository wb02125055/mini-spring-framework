package com.wb.springframework.core.type.classreading;

import com.wb.springframework.core.io.Resource;
import com.wb.springframework.core.type.AnnotationMetadata;
import com.wb.springframework.core.type.ClassMetadata;

/**
 * @author WangBing
 * @date 2023/6/17 22:11
 */
public interface MetadataReader {

    Resource getResource();

    ClassMetadata getClassMetadata();

    AnnotationMetadata getAnnotationMetadata();
}

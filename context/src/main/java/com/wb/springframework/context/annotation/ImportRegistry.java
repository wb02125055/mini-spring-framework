package com.wb.springframework.context.annotation;

import com.wb.springframework.core.type.AnnotationMetadata;

/**
 * @author WangBing
 * @date 2023/7/1 10:19
 */
public interface ImportRegistry {

    AnnotationMetadata getImportingClassFor(String importedClass);

    void removeImportingClass(String importingClass);


}

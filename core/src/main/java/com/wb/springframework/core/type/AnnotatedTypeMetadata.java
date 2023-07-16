package com.wb.springframework.core.type;

import java.util.Map;

/**
 * @author WangBing
 * @date 2023/5/21 17:05
 */
public interface AnnotatedTypeMetadata {
    /**
     * 判断某个类是否被给定名称的注解所标注
     * @param annotationName 注解名称
     * @return 是否被标注了
     */
    boolean isAnnotated(String annotationName);

    Map<String, Object> getAnnotationAttributes(String annotationName, boolean classValuesAsString);

}

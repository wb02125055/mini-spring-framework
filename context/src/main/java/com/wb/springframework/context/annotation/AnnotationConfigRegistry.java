package com.wb.springframework.context.annotation;

/**
 * @author WangBing
 * @date 2023/5/20 23:17
 */
public interface AnnotationConfigRegistry {
    void register(Class<?>... componentClasses);

    void scan(String... basePackages);
}

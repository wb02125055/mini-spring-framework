package com.wb.springframework.context.annotation;

import java.lang.annotation.*;

/**
 * @author WangBing
 * @date 2023/7/1 20:26
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface ComponentScans {
    ComponentScan[] value();
}

package com.wb.springframework.stereotype;

import java.lang.annotation.*;

/**
 * 标记一个普通组件
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Indexed
public @interface Component {
    String value() default "";
}

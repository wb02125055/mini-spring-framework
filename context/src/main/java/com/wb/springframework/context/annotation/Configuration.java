package com.wb.springframework.context.annotation;

import com.wb.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 用来标记一个配置类
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Configuration {
}

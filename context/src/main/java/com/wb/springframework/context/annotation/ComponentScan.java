package com.wb.springframework.context.annotation;

import com.wb.springframework.beans.factory.support.BeanNameGenerator;
import com.wb.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ComponentScan {

    @AliasFor("basePackages")
    String[] value() default {};

    @AliasFor("value")
    String[] basePackages() default {};

    boolean useDefaultFilters() default true;


    Class<? extends BeanNameGenerator> nameGenerator() default BeanNameGenerator.class;


}

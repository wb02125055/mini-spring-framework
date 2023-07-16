package com.wb.springframework.test.beans;

import com.wb.springframework.context.ApplicationContext;
import com.wb.springframework.context.annotation.AnnotationConfigApplicationContext;
import com.wb.springframework.test.beans.config.BeanConfig;

/**
 * @author WangBing
 * @date 2023/5/19 23:03
 */
public class TestMain {
    public static void main(String[] args) {
        ApplicationContext acx = new AnnotationConfigApplicationContext(BeanConfig.class);
        System.out.println("aaa");
        Object cat = acx.getBean("cat");
        System.out.println("cat: " + cat);
    }
}

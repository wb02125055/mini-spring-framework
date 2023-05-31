package com.wb.springframework.test.beans.config;

import com.wb.springframework.context.annotation.Bean;
import com.wb.springframework.context.annotation.ComponentScan;
import com.wb.springframework.context.annotation.Configuration;
import com.wb.springframework.test.beans.model.Cat;

/**
 * @author WangBing
 * @date 2023/5/19 23:04
 */
@Configuration
@ComponentScan(basePackages = "com.wb.springframework.test.beans")
public class BeanConfig {

    @Bean("cat")
    public Cat whiteCat() {
        Cat cat = new Cat();
        cat.setAge(3);
        cat.setName("tomcat");
        cat.setColor("white");
        return cat;
    }
}

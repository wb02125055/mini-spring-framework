package com.wb.springframework.context.annotation;

import com.wb.springframework.context.support.GenericApplicationContext;

/**
 * @author WangBing
 * @date 2023/5/18 22:41
 */
public class AnnotationConfigApplicationContext extends GenericApplicationContext implements AnnotationConfigRegistry {

    private final AnnotatedBeanDefinitionReader reader;

    public AnnotationConfigApplicationContext() {
        this.reader = new AnnotatedBeanDefinitionReader(this);
    }
    public AnnotationConfigApplicationContext(Class<?> ...componentClasses) {
        this();

        // 注册配置类对应的bean定义
        register(componentClasses);

        // 刷新容器
        refresh();
    }

    @Override
    public void register(Class<?>... componentClasses) {
        this.reader.register(componentClasses);
    }

    @Override
    public void scan(String... basePackages) {

    }
}

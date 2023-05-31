package com.wb.springframework.beans.factory.config;

import com.wb.springframework.beans.BeanMetadataElement;
import com.wb.springframework.beans.MutablePropertyValues;
import com.wb.springframework.core.AttributeAccessor;

/**
 * @author WangBing
 * @date 2023/5/7 10:06
 */
public interface BeanDefinition extends AttributeAccessor, BeanMetadataElement {

    String SCOPE_SINGLETON = "singleton";

    String SCOPE_PROTOTYPE = "prototype";

    /**
     * bean的角色，表示用户在应用中自定义的bean
     */
    int ROLE_APPLICATION = 0;

    /**
     * 用来表示复杂的bean定义
     */
    int ROLE_SUPPORT = 1;
    /**
     * 用来表示内部使用的基础设施bean
     */
    int ROLE_INFRASTRUCTURE = 2;

    /**
     * 设置父级bean的名称
     * @param name name
     */
    void setParentName(String name);

    /**
     * 获取父级bean的名称
     */
    String getParentName();

    /**
     * 设置和获取bean定义中使用的class的名称
     * @param beanClassName beanClassName
     */
    void setBeanClassName(String beanClassName);
    String getBeanClassName();

    /**
     * 设置和获取bean的作用域
     * @param scope scope
     */
    void setScope(String scope);
    String getScope();

    /**
     * 设置和判断当前的bean是否是延迟加载模式
     * @param lazyInit lazyInit
     */
    void setLazyInit(boolean lazyInit);
    boolean isLazyInit();

    /**
     * 设置和获取当前bean所依赖的其他bean
     * @param dependsOn dependsOn
     */
    void setDependsOn(String ...dependsOn);
    String[] getDependsOn();

    /**
     * 设置和获取当前的bean是否可以通过setter方法来作为一个候选的bean装配到其他的bean里面
     * @param autowireCandidate autowireCandidate
     */
    void setAutowireCandidate(boolean autowireCandidate);
    boolean isAutowireCandidate();

    /**
     * 设置和获取当前的bean是否为优先装配的bean
     * @param primary 当容器中存在着多个与目标类型匹配的bean时，使用primary可以设置当前bean为优先被装配的bean
     */
    void setPrimary(boolean primary);
    boolean isPrimary();

    /**
     * 设置和获取当前bean对应的工厂bean的名称
     * @param factoryBeanName factoryBeanName
     */
    void setFactoryBeanName(String factoryBeanName);
    String getFactoryBeanName();

    /**
     * 获取当前bean的工厂方法的名称
     * @return factoryBeanName
     */
    String getFactoryMethodName();

    // ConstructorArgumentValues

    /**
     * 获取当前bean的属性值，在获取bean实例的时候，会将所有的bean的属性(xml, javaconfig)中配置的属性值解析到PrpertyValues中
     * @return
     */
    MutablePropertyValues getPropertyValues();

    /**
     * 判断当前的bean定义中是否有ProptyValues
     * @return 是否有propertyValues
     */
    default boolean hasPropertyValues() {
        return !getPropertyValues().isEmpty();
    }

    /**
     * 设置和获取bean的初始化方法名称
     * @param initMethodName initMethodName
     */
    void setInitMethodName(String initMethodName);

    /**
     * 获取初始化方法的名称
     * @return 初始化方法的名称
     */
    String getInitMethodName();

    /**
     * 设置和获取bean的销毁方法
     * @param destroyMethodName destroyMethodName
     */
    void setDestroyMethodName(String destroyMethodName);
    String getDestroyMethodName();

    /**
     * 设置和获取bean定义的角色(简单的bean，自定义的复杂的bean，基础设施bean)
     * @param role bean定义的角色
     */
    void setRole(int role);
    int getRole();


    /**
     * 设置和获取bean定义的描述信息
     * @param description description
     */
    void setDescription(String description);
    String getDescription();

    /**
     * 判断bean是否为singleton
     */
    boolean isSingleton();
    /**
     * 判断bean是否为prototype
     */
    boolean isPrototype();
    /**
     * 判断bean是否为抽象的，如果bean被定义为抽象的，那么就不是需要被初始化的
     */
    boolean isAbstract();

    /**
     * 获取当前bean的资源描述，用于出现错误时显示上下文信息
     */
    String getResourceDescription();

    /**
     * 获取当前bean对应的原始bean定义
     * @return 原始bean定义
     */
    BeanDefinition getOriginatingBeanDefinition();
}

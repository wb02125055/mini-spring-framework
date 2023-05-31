package com.wb.springframework.beans.factory.support;

import com.wb.springframework.beans.BeanMetadataAttributeAccessor;
import com.wb.springframework.beans.MutablePropertyValues;
import com.wb.springframework.beans.factory.config.BeanDefinition;
import com.wb.springframework.util.ClassUtils;

import java.util.function.Supplier;

/**
 * @author WangBing
 * @date 2023/5/18 22:21
 */
public abstract class AbstractBeanDefinition extends BeanMetadataAttributeAccessor implements BeanDefinition {

    public static final String SCOPE_DEFAULT = "";

    private String scope = SCOPE_DEFAULT;

    private volatile Object beanClass;

    private Supplier<?> instanceSupplier;

    private boolean synthetic = false;

    private boolean abstractFlag = false;

    /**
     * 允许访问非public得构造器和方法
     */
    private boolean nonPublicAccessAllowed = true;

    protected AbstractBeanDefinition() {
//        this(null, null);
    }

    protected AbstractBeanDefinition(BeanDefinition original) {
        setParentName(original.getParentName());
        setBeanClassName(original.getBeanClassName());
    }


    public void setBeanClass(Object beanClass) {
        this.beanClass = beanClass;
    }
    public Class<?> getBeanClass() throws IllegalStateException {
        Object beanClassObject = this.beanClass;
        if (beanClassObject == null) {
            throw new IllegalStateException("No bean class specified on bean definition");
        }
        if (!(beanClassObject instanceof Class)) {
            throw new IllegalStateException("Bean class name '" + beanClassObject
                    + "' has not been resolved into an actual Class");
        }
        return (Class<?>) beanClassObject;
    }
    public boolean hasBeanClass() {
        return this.beanClass instanceof Class;
    }

    @Override
    public Object getSource() {
        return null;
    }

    @Override
    public void setParentName(String name) {

    }

    public boolean isNonPublicAccessAllowed() {
        return nonPublicAccessAllowed;
    }

    public void setNonPublicAccessAllowed(boolean nonPublicAccessAllowed) {
        this.nonPublicAccessAllowed = nonPublicAccessAllowed;
    }

    public boolean isSynthetic() {
        return synthetic;
    }

    public void setSynthetic(boolean synthetic) {
        this.synthetic = synthetic;
    }

    @Override
    public String getParentName() {
        return null;
    }

    @Override
    public void setBeanClassName(String beanClassName) {
        this.beanClass = beanClassName;
    }

    @Override
    public String getBeanClassName() {
        Object beanClassObject = this.beanClass;
        if (beanClassObject instanceof Class) {
            return ((Class<?>) beanClassObject).getName();
        }
        return (String) beanClassObject;
    }

    @Override
    public void setScope(String scope) {
        this.scope = scope;
    }

    @Override
    public String getScope() {
        return this.scope;
    }

    @Override
    public void setLazyInit(boolean lazyInit) {

    }

    @Override
    public boolean isLazyInit() {
        return false;
    }

    @Override
    public void setDependsOn(String... dependsOn) {

    }

    @Override
    public String[] getDependsOn() {
        return new String[0];
    }

    @Override
    public void setAutowireCandidate(boolean autowireCandidate) {

    }

    @Override
    public boolean isAutowireCandidate() {
        return false;
    }

    @Override
    public void setPrimary(boolean primary) {

    }

    @Override
    public boolean isPrimary() {
        return false;
    }

    @Override
    public void setFactoryBeanName(String factoryBeanName) {

    }

    @Override
    public String getFactoryBeanName() {
        return null;
    }

    @Override
    public String getFactoryMethodName() {
        return null;
    }

    @Override
    public MutablePropertyValues getPropertyValues() {
        return null;
    }

    @Override
    public boolean hasPropertyValues() {
        return BeanDefinition.super.hasPropertyValues();
    }

    @Override
    public void setInitMethodName(String initMethodName) {

    }

    @Override
    public String getInitMethodName() {
        return null;
    }

    @Override
    public void setDestroyMethodName(String destroyMethodName) {

    }

    @Override
    public String getDestroyMethodName() {
        return null;
    }

    @Override
    public void setRole(int role) {

    }

    @Override
    public int getRole() {
        return 0;
    }

    @Override
    public void setDescription(String description) {

    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public boolean isSingleton() {
        return SCOPE_SINGLETON.equals(this.scope) || SCOPE_DEFAULT.equals(this.scope);
    }

    @Override
    public boolean isPrototype() {
        return false;
    }

    public void setAbstractFlag(boolean abstractFlag) {
        this.abstractFlag = abstractFlag;
    }

    @Override
    public boolean isAbstract() {
        return this.abstractFlag;
    }

    @Override
    public String getResourceDescription() {
        return null;
    }

    @Override
    public BeanDefinition getOriginatingBeanDefinition() {
        return null;
    }

    public Class<?> resolveBeanClass(ClassLoader classLoader) throws ClassNotFoundException {
        String className = getBeanClassName();
        if (null == className) {
            return null;
        }
        Class<?> resolvedClass = ClassUtils.forName(className, classLoader);
        this.beanClass = resolvedClass;
        return resolvedClass;
    }

    public Supplier<?> getInstanceSupplier() {
        return instanceSupplier;
    }

    public void setInstanceSupplier(Supplier<?> instanceSupplier) {
        this.instanceSupplier = instanceSupplier;
    }

    protected abstract AbstractBeanDefinition cloneBeanDefinition();
}

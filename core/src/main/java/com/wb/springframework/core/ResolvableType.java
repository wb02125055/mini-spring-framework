package com.wb.springframework.core;

import com.sun.org.apache.xpath.internal.operations.Variable;
import com.wb.springframework.util.ClassUtils;
import com.wb.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.lang.reflect.*;
import java.util.Map;

/**
 * @author WangBing
 * @date 2023/5/21 21:46
 */
public class ResolvableType implements Serializable {

    public static final ResolvableType NONE = new ResolvableType(EmptyType.INSTANCE, null, null, 0);

    private static final ResolvableType[] EMPTY_TYPE_ARRAY = new ResolvableType[0];

    private final Type type;

    private final SerializableTypeWrapper.TypeProvider typeProvider;

    private final VariableResolver variableResolver;

    private final ResolvableType componentType;

    private final Integer hash;

    private Class<?> resolved;

    private ResolvableType(Type type, SerializableTypeWrapper.TypeProvider typeProvider,
                           VariableResolver variableResolver) {
        this.type = type;
        this.typeProvider = typeProvider;
        this.variableResolver = variableResolver;
        this.componentType = null;
        this.hash = calculateHashCode();
        this.resolved = null;
    }

    private ResolvableType(Type type, SerializableTypeWrapper.TypeProvider typeProvider,
                           VariableResolver variableResolver, Integer hash) {
        this.type = type;
        this.typeProvider = typeProvider;
        this.variableResolver = variableResolver;
        this.hash = hash;
        this.componentType = null;
        this.resolved = resolveClass();
    }

    private ResolvableType(Type type, SerializableTypeWrapper.TypeProvider typeProvider,
                           VariableResolver variableResolver, ResolvableType componentType) {
        this.type = type;
        this.typeProvider = typeProvider;
        this.variableResolver = variableResolver;
        this.componentType = componentType;
        this.hash = calculateHashCode();
        this.resolved = null;
    }

    private ResolvableType(Class<?> clazz) {
        this.resolved = null != clazz ? clazz : Object.class;
        this.type = this.resolved;
        this.typeProvider = null;
        this.variableResolver = null;
        this.componentType = null;
        this.hash = null;
    }

    public ResolvableType[] getGenerics() {
        return null;
    }

    public boolean isAssignableFrom(Class<?> other) {
        return isAssignableFrom(forClass(other), null);
    }

    public boolean hasGenerics() {
        return getGenerics().length > 0;
    }

    private Class<?> resolveClass() {
        if (this.type == EmptyType.INSTANCE) {
            return null;
        }
        if (this.type instanceof Class) {
            return (Class<?>) this.type;
        }
        if (this.type instanceof GenericArrayType) {
            Class<?> resolvedComponent = getComponentType().resolve();
            return resolvedComponent != null ? Array.newInstance(resolvedComponent, 0).getClass() : null;
        }
        return resolveType().resolve();
    }

    public Class<?> resolve() {
        return this.resolved;
    }

    public boolean isAssignableFrom(ResolvableType other) {
        return isAssignableFrom(other, null);
    }

    private boolean isAssignableFrom(ResolvableType other, Map<Type, Type> matchedBefore) {
        if (this == NONE || other == NONE) {
            return false;
        }
        if (isArray()) {
            return other.isArray() && getComponentType().isAssignableFrom(other.getComponentType());
        }
        return true;
    }

    public ResolvableType getComponentType() {
        if (this == NONE) {
            return NONE;
        }
        if (this.componentType != null) {
            return this.componentType;
        }
        return resolveType().getComponentType();
    }

    public boolean isInstance(Object obj) {
        return obj != null && isAssignableFrom(obj.getClass());
    }

    public boolean isArray() {
        if (this == NONE) {
            return false;
        }
        return (this.type instanceof Class && ((Class<?>) this.type).isArray()) ||
                this.type instanceof GenericArrayType || resolveType().isArray();
    }

    ResolvableType resolveType() {
        if (this.type instanceof ParameterizedType) {
            return forType(((ParameterizedType) this.type).getRawType(), this.variableResolver);
        }
        return NONE;
    }

    static ResolvableType forType(Type type, VariableResolver variableResolver) {
        return forType(type, null, variableResolver);
    }

    static ResolvableType forType(
            Type type, SerializableTypeWrapper.TypeProvider typeProvider, VariableResolver variableResolver) {
        if (type == null && typeProvider != null) {
            type = SerializableTypeWrapper.forTypeProvider(typeProvider);
        }
        if (type == null) {
            return NONE;
        }
        ResolvableType resolvableType = new ResolvableType(type, typeProvider, variableResolver);
        // todo
        return resolvableType;
    }

    public static ResolvableType forClass(Class<?> clazz) {
        return new ResolvableType(clazz);
    }

    private int calculateHashCode() {
        int hashCode = ObjectUtils.nullSafeHashCode(this.type);
        if (this.typeProvider != null) {
            hashCode = 31 * ObjectUtils.nullSafeHashCode(this.typeProvider.getType());
        }
        if (this.variableResolver != null) {
            hashCode = 31 * hashCode + ObjectUtils.nullSafeHashCode(this.variableResolver.getSource());
        }
        if (this.componentType != null) {
            hashCode = 31 * hashCode + ObjectUtils.nullSafeHashCode(this.componentType);
        }
        return hashCode;
    }



    public static ResolvableType forRawClass(Class<?> clazz) {
        return new ResolvableType(clazz) {
            @Override
            public ResolvableType[] getGenerics() {
                return EMPTY_TYPE_ARRAY;
            }

            @Override
            public boolean isAssignableFrom(Class<?> other) {
                return null == clazz || ClassUtils.isAssignable(clazz, other);
            }

            @Override
            public boolean isAssignableFrom(ResolvableType other) {
                Class<?> otherClass = other.resolve();
                return null != otherClass && (clazz == null || ClassUtils.isAssignable(clazz, otherClass));
            }
        };
    }

    static class EmptyType implements Type, Serializable {
        static final Type INSTANCE = new EmptyType();
        Object readResolve() {
            return INSTANCE;
        }
    }

    interface VariableResolver extends Serializable {
        Object getSource();

        ResolvableType resolveVariable(TypeVariable<?> variable);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return this.hash != null ? this.hash : calculateHashCode();
    }
}

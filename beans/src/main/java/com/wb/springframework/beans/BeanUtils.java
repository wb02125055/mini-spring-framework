package com.wb.springframework.beans;

import com.wb.springframework.util.ReflectionUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author WangBing
 * @date 2023/5/28 18:16
 */
public abstract class BeanUtils {
    public static Object instantiateClass(Constructor<?> ctor, Object... args) throws BeanInstantiationException {
        try {
            ReflectionUtils.makeAccessible(ctor);
            return ctor.newInstance(args);
        } catch (InstantiationException e) {
            throw new BeanInstantiationException("Is it an abstract class?", e);
        } catch (IllegalAccessException e) {
            throw new BeanInstantiationException("Is the constructor accessible?", e);
        } catch (IllegalArgumentException e) {
            throw new BeanInstantiationException("Illegal arguments for constructor", e);
        } catch (InvocationTargetException e) {
            throw new BeanInstantiationException("Constructor threw unknown exception", e.getTargetException());
        }
    }
}

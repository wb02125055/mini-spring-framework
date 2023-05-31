package com.wb.springframework.util;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * @author WangBing
 * @date 2023/5/21 14:50
 */
public abstract class ClassUtils {

    public static final String ARRAY_SUFFIX = "[]";

    public static final String INTERNAL_ARRAY_PREFIX = "[";

    public static final String NON_PRIMITIVE_ARRAY_PREFIX = "[L";

    private static final char PACKAGE_SEPARATOR = '.';

    public static final String CGLIB_CLASS_SEPARATOR = "$$";

    private static final char INNER_CLASS_SEPARATOR = '$';

    private static final Map<String, Class<?>> primitiveTypeNameMap = new HashMap<>(32);

    private static final Map<String, Class<?>> commonClassCache = new HashMap<>(64);

    // TODO ??? IdentityHashMap
    private static final Map<Class<?>, Class<?>> primitiveWrapperTypeMap = new IdentityHashMap<>(8);

    private static final Map<Class<?>, Class<?>> primitiveTypeToWrapperMap = new IdentityHashMap<>(8);

    static {
        primitiveWrapperTypeMap.put(Boolean.class, boolean.class);
        primitiveWrapperTypeMap.put(Byte.class, byte.class);
        primitiveWrapperTypeMap.put(Character.class, char.class);
        primitiveWrapperTypeMap.put(Double.class, double.class);
        primitiveWrapperTypeMap.put(Float.class, float.class);
        primitiveWrapperTypeMap.put(Integer.class, int.class);
        primitiveWrapperTypeMap.put(Long.class, long.class);
        primitiveWrapperTypeMap.put(Short.class, short.class);
        primitiveWrapperTypeMap.put(Void.class, void.class);

        for (Map.Entry<Class<?>, Class<?>> entry : primitiveWrapperTypeMap.entrySet()) {
            primitiveTypeToWrapperMap.put(entry.getValue(), entry.getKey());
            registerCommonClasses(entry.getKey());
        }
    }

    private static void registerCommonClasses(Class<?>... commonClasses) {
        for (Class<?> clazz : commonClasses) {
            commonClassCache.put(clazz.getName(), clazz);
        }
    }

    public static String getShortName(String beanClassName) {
        int lastDotIndex = beanClassName.lastIndexOf(PACKAGE_SEPARATOR);
        int nameEndIndex = beanClassName.indexOf(CGLIB_CLASS_SEPARATOR);
        if (nameEndIndex == -1) {
            nameEndIndex = beanClassName.length();
        }
        String shortName = beanClassName.substring(lastDotIndex + 1, nameEndIndex);
        shortName = shortName.replace(INNER_CLASS_SEPARATOR, PACKAGE_SEPARATOR);
        return shortName;
    }

    public static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable ex) {
            // no op
        }
        if (null == cl) {
            cl = ClassUtils.class.getClassLoader();
            if (cl == null) {
                try {
                    cl = ClassLoader.getSystemClassLoader();
                } catch (Throwable ex) {
                    // no op
                }
            }
        }
        return cl;
    }

    // lhsType: left-hand side type
    // rhsType: right-hand side type
    public static boolean isAssignable(Class<?> lhsType, Class<?> rhsType) {
        if (lhsType.isAssignableFrom(rhsType)) {
            return true;
        }
        if (lhsType.isPrimitive()) {
            Class<?> resolvedPrimitive = primitiveWrapperTypeMap.get(rhsType);
            if (lhsType == resolvedPrimitive) {
                return true;
            }
        } else {
            Class<?> resolveWrapper = primitiveTypeToWrapperMap.get(rhsType);
            if (resolveWrapper != null && lhsType.isAssignableFrom(resolveWrapper)) {
                return true;
            }
        }
        return false;
    }

    public static Class<?> forName(String name, ClassLoader classLoader)
        throws ClassNotFoundException {
        Class<?> clazz = resolvePrimitiveClassName(name);
        if (clazz == null) {
            clazz = commonClassCache.get(name);
        }
        if (clazz != null) {
            return clazz;
        }
        // java.lang.String[]
        if (name.endsWith(ARRAY_SUFFIX)) {
            String elementClassName = name.substring(0, name.length() - ARRAY_SUFFIX.length());
            Class<?> elementClass = forName(elementClassName, classLoader);
            return Array.newInstance(elementClass, 0).getClass();
        }
        // [Ljava.lang.String;
        if (name.startsWith(NON_PRIMITIVE_ARRAY_PREFIX) && name.endsWith(";")) {
            String elementName = name.substring(NON_PRIMITIVE_ARRAY_PREFIX.length(), name.length() - 1);
            Class<?> elementClass = forName(elementName, classLoader);
            return Array.newInstance(elementClass, 0).getClass();
        }
        // [[I or [[Ljava.lang.String;
        if (name.startsWith(INTERNAL_ARRAY_PREFIX)) {
            String elementName = name.substring(INTERNAL_ARRAY_PREFIX.length());
            Class<?> elementClass = forName(elementName, classLoader);
            return Array.newInstance(elementClass, 0).getClass();
        }
        ClassLoader classLoaderToUse = classLoader;
        if (classLoaderToUse == null) {
            classLoaderToUse = getDefaultClassLoader();
        }
        try {
            return Class.forName(name, false, classLoaderToUse);
        } catch (ClassNotFoundException ex) {
            int lastDotIndex = name.lastIndexOf(PACKAGE_SEPARATOR);
            if (lastDotIndex != -1) {
                String innerClassName = name.substring(0, lastDotIndex) + INNER_CLASS_SEPARATOR +
                        name.substring(lastDotIndex + 1);
                try {
                    return Class.forName(innerClassName, false, classLoaderToUse);
                } catch (ClassNotFoundException exception) {
                    // no op
                }
            }
            throw ex;
        }
    }


    public static Class<?> resolvePrimitiveClassName(String name) {
        Class<?> result = null;
        if (name != null && name.length() <= 8) {
            result = primitiveTypeNameMap.get(name);
        }
        return result;
    }


    public static Class<?> getUserClass(Object instance) {
        return getUserClass(instance.getClass());
    }

    public static Class<?> getUserClass(Class<?> clazz) {
        if (clazz.getName().contains(CGLIB_CLASS_SEPARATOR)) {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null && superClass != Object.class) {
                return superClass;
            }
        }
        return clazz;
    }
}

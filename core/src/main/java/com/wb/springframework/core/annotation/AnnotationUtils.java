package com.wb.springframework.core.annotation;

import com.wb.springframework.util.ConcurrentReferenceHashMap;
import com.wb.springframework.util.ReflectionUtils;
import com.wb.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author WangBing
 * @date 2023/6/18 17:31
 */
public abstract class AnnotationUtils {

    public static final String VALUE = "value";

    private static final Map<AnnotatedElement, Annotation[]> declaredAnnotationsCache =
            new ConcurrentReferenceHashMap<>(256);

    static Annotation[] getDeclaredAnnotations(AnnotatedElement element) {
        if (element instanceof Class || element instanceof Member) {
            return declaredAnnotationsCache.computeIfAbsent(element, AnnotatedElement::getDeclaredAnnotations);
        }
        return element.getDeclaredAnnotations();
    }


    public static boolean isInJavaLangAnnotationPackage(Class<? extends Annotation> annotationType) {
        // 当前的注解是否以"java.lang.annotation"开头的
        return annotationType != null && isInJavaLangAnnotationPackage(annotationType.getName());
    }


    public static boolean isInJavaLangAnnotationPackage(String annotationType) {
        return annotationType != null && annotationType.startsWith("java.lang.annotation");
    }


    public static Object getValue(Annotation annotation) {
        return getValue(annotation, VALUE);
    }


    public static Object getValue(Annotation annotation, String attributeName) {
        if (annotation == null || !StringUtils.hasText(attributeName)) {
            return null;
        }
        try {
            Method method = annotation.annotationType().getDeclaredMethod(attributeName);
            ReflectionUtils.makeAccessible(method);
            return method.invoke(annotation);
        } catch (Throwable e) {
            return null;
        }
    }


    public static boolean hasPlainJavaAnnotationsOnly(Object annotatedElement) {
        Class<?> clazz;
        if (annotatedElement instanceof Class) {
            clazz = (Class<?>) annotatedElement;
        } else if (annotatedElement instanceof Member) {
            clazz = ((Member) annotatedElement).getDeclaringClass();
        } else {
            return false;
        }
        String className = clazz.getName();
        return className.startsWith("java.") || className.startsWith("com.wb.springframework.lang.");
    }
}

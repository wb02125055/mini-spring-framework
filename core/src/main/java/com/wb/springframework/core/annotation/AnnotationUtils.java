package com.wb.springframework.core.annotation;

import com.wb.springframework.util.ConcurrentReferenceHashMap;
import com.wb.springframework.util.ObjectUtils;
import com.wb.springframework.util.ReflectionUtils;
import com.wb.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

/**
 * @author WangBing
 * @date 2023/6/18 17:31
 */
public abstract class AnnotationUtils {

    public static final String VALUE = "value";

    private static final Map<AnnotatedElement, Annotation[]> declaredAnnotationsCache =
            new ConcurrentReferenceHashMap<>(256);

    private static final Map<Class<? extends Annotation>, List<Method>> attributeMethodsCache =
            new ConcurrentReferenceHashMap<>(256);

    private static final Map<Class<? extends Annotation>, Boolean> synthesizableCache =
            new ConcurrentReferenceHashMap<>(256);

    private static final Map<Class<? extends Annotation>, Map<String, List<String>>> attributeAliasesCache =
            new ConcurrentReferenceHashMap<>(256);


    private static final Map<Method, AliasDescriptor> aliasDescriptorCache =
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

    public static void validateAnnotation(Annotation annotation) {
        for (Method method : getAttributeMethods(annotation.annotationType())) {
            Class<?> returnType = method.getReturnType();
            if (returnType == Class.class || returnType == Class[].class) {
                try {
                    method.invoke(annotation);
                } catch (Throwable ex) {
                    throw new IllegalStateException("Could not obtain annotation attribute value for " + method, ex);
                }
            }
        }
    }

    static List<Method> getAttributeMethods(Class<? extends Annotation> annotationType) {
        List<Method> methods = attributeMethodsCache.get(annotationType);
        if (methods != null) {
            return methods;
        }
        methods = new ArrayList<>();
        for (Method method : annotationType.getDeclaredMethods()) {
            if (isAttributeMethod(method)) {
                ReflectionUtils.makeAccessible(method);
                methods.add(method);
            }
        }
        attributeMethodsCache.put(annotationType, methods);
        return methods;
    }

    static boolean isAttributeMethod(Method method) {
        return method != null && method.getParameterCount() == 0 && method.getReturnType() != void.class;
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

    static String getAttributeOverrideName(Method attribute, Class<? extends Annotation> metaAnnotationType) {
        AliasDescriptor descriptor = AliasDescriptor.from(attribute);
        return descriptor != null && metaAnnotationType != null ?
                descriptor.getAttributeOverrideName(metaAnnotationType) : null;
    }

    static void postProcessAnnotationAttributes(Object annotatedElement,
                                                AnnotationAttributes attributes, boolean classValuesAsString, boolean nestedAnnotationsAsMap) {
        if (attributes == null) {
            return;
        }
        Class<? extends Annotation> annotationType = attributes.annotationType();

        // 跟踪哪些属性的值已经被替换过，这样可以缩短搜索算法的耗时
        Set<String> valuesAlreadyReplaced = new HashSet<>();
        if (!attributes.validated) {
            // 校验 @AliasFor 配置
            Map<String, List<String>> aliasMap = getAttributeAliasMap(annotationType);
            aliasMap.forEach((attributeName, aliasedAttributeNames) -> {
                if (valuesAlreadyReplaced.contains(attributeName)) {
                    return;
                }
                // todo
                Object value = attributes.get(attributeName);
                boolean valuePresent = (value != null && !(value instanceof DefaultValueHolder));
                for (String aliasedAttributeName : aliasedAttributeNames) {
                    if (valuesAlreadyReplaced.contains(aliasedAttributeName)) {
                        continue;
                    }
                    Object aliasedValue = attributes.get(aliasedAttributeName);
                    boolean aliasPresent = (aliasedValue != null && !(aliasedValue instanceof DefaultValueHolder));
                    // 校验或者使用alias做替换.
                    if (valuePresent || aliasPresent) {
                        if (valuePresent && aliasPresent) {
                            if (!ObjectUtils.nullSafeEquals(value, aliasedValue)) {
                                String elementAsString = annotatedElement != null ? annotatedElement.toString() : "unknown element";
                                throw new AnnotationConfigurationException(String.format(
                                        "In AnnotationAttributes for annotation [%s] declared on %s.", attributes.displayName,
                                        elementAsString));
                            }
                        } else if (aliasPresent) {
                            // 使用aliasedValue来替换value
                            attributes.put(attributeName, adaptValue(annotatedElement, aliasedValue, classValuesAsString, nestedAnnotationsAsMap));
                            valuesAlreadyReplaced.add(attributeName);
                        } else {
                            // 使用value来替换aliasedValue
                            attributes.put(aliasedAttributeName, adaptValue(annotatedElement, value, classValuesAsString, nestedAnnotationsAsMap));
                            valuesAlreadyReplaced.add(aliasedAttributeName);
                        }
                    }
                }

            });
            attributes.validated = true;
        }

        // 通过默认值来替换剩余的占位符
        for (Map.Entry<String, Object> attributeEntry : attributes.entrySet()) {
            String attributeName = attributeEntry.getKey();
            if (valuesAlreadyReplaced.contains(attributeName)) {
                continue;
            }
            Object value = attributeEntry.getValue();
            if (value instanceof DefaultValueHolder) {
                value = ((DefaultValueHolder) value).defaultValue;
                attributes.put(attributeName,
                        adaptValue(annotatedElement, value, classValuesAsString, nestedAnnotationsAsMap));
            }
        }

    }

    static AnnotationAttributes retrieveAnnotationAttributes(Object annotatedElement,
                                                             Annotation annotation, boolean classValuesAsString, boolean nestedAnnotationAsMap) {
        Class<? extends Annotation> annotationType = annotation.annotationType();
        AnnotationAttributes attributes = new AnnotationAttributes(annotationType);
        for (Method method : getAttributeMethods(annotationType)) {
            try {
                Object attributeValue = method.invoke(annotation);
                Object defaultValue = method.getDefaultValue();
                if (defaultValue != null && ObjectUtils.nullSafeEquals(attributeValue, defaultValue)) {
                    attributeValue = new DefaultValueHolder(defaultValue);
                }
                attributes.put(method.getName(), adaptValue(annotatedElement, attributeValue, classValuesAsString, nestedAnnotationAsMap));
            } catch (Throwable ex) {
                throw new IllegalStateException("Could not obtain annotation attribute value for " + method, ex);
            }
        }
        return attributes;
    }

    static Object adaptValue(Object annotatedElement, Object value,
                             boolean classValuesAsString, boolean nestedAnnotationAsMap) {
        if (classValuesAsString) {
            if (value instanceof Class) {
                return ((Class<?>) value).getName();
            } else if (value instanceof Class[]) {
                Class<?>[] clazzArray = (Class<?>[]) value;
                String[] classNames = new String[clazzArray.length];
                for (int i = 0; i < clazzArray.length; i++) {
                    classNames[i] = clazzArray[i].getName();
                }
                return classNames;
            }
        }
        if (value instanceof Annotation) {
            Annotation annotation = (Annotation) value;
            if (nestedAnnotationAsMap) {
                return getAnnotationAttributes(annotatedElement, annotation, classValuesAsString, true);
            } else {
                return synthesizeAnnotation(annotation, annotatedElement);
            }
        }
        if (value instanceof Annotation[]) {
            Annotation[] annotations = (Annotation[]) value;
            if (nestedAnnotationAsMap) {
                AnnotationAttributes[] mappedAnnotations = new AnnotationAttributes[annotations.length];
                for (int i = 0; i < annotations.length; i++) {
                    mappedAnnotations[i] =
                            getAnnotationAttributes(annotatedElement, annotations[i], classValuesAsString, true);
                }
                return mappedAnnotations;
            } else {
                return synthesizeAnnotationArray(annotations, annotatedElement);
            }
        }
        return value;
    }

    static Annotation[] synthesizeAnnotationArray(Annotation[] annotations, Object annotatedElement) {
        if (hasPlainJavaAnnotationsOnly(annotatedElement)) {
            return annotations;
        }
        Annotation[] synthesized = (Annotation[]) Array.newInstance(annotations.getClass().getComponentType(), annotations.length);
        for (int i = 0; i < annotations.length; i++) {
            synthesized[i] = synthesizeAnnotation(annotations[i], annotatedElement);
        }
        return synthesized;
    }

    @SuppressWarnings("unchecked")
    static <A extends Annotation> A synthesizeAnnotation(A annotation, Object annotatedElement) {
        if (annotation instanceof SynthesizedAnnotation || hasPlainJavaAnnotationsOnly(annotatedElement)) {
            return annotation;
        }
        Class<? extends Annotation> annotationType = annotation.annotationType();
        if (!isSynthesizable(annotationType)) {
            return annotation;
        }

        DefaultAnnotationAttributeExtractor attributeExtractor =
                new DefaultAnnotationAttributeExtractor(annotation, annotatedElement);
        InvocationHandler handler = new SynthesizedAnnotationInvocationHandler(attributeExtractor);

        // Can always expose Spring's SynthesizedAnnotation marker since we explicitly check for a
        // synthesizable annotation before (which needs to declare @AliasFor from the same package)
        Class<?>[] exposedInterfaces = new Class<?>[]{annotationType, SynthesizedAnnotation.class};
        return (A) Proxy.newProxyInstance(annotation.getClass().getClassLoader(), exposedInterfaces, handler);
    }

    @SuppressWarnings("unchecked")
    private static boolean isSynthesizable(Class<? extends Annotation> annotationType) {
        if (hasPlainJavaAnnotationsOnly(annotationType)) {
            return false;
        }
        Boolean synthesizable = synthesizableCache.get(annotationType);
        if (synthesizable != null) {
            return synthesizable;
        }
        synthesizable = Boolean.FALSE;
        for (Method attribute : getAttributeMethods(annotationType)) {
            if (!getAttributeAliasNames(attribute).isEmpty()) {
                synthesizable = Boolean.TRUE;
                break;
            }
            Class<?> returnType = attribute.getReturnType();
            if (Annotation[].class.isAssignableFrom(returnType)) {
                Class<? extends Annotation> nestedAnnotationType =
                        (Class<? extends Annotation>) returnType.getComponentType();
                if (isSynthesizable(nestedAnnotationType)) {
                    synthesizable = Boolean.TRUE;
                    break;
                }
            } else if (Annotation.class.isAssignableFrom(returnType)) {
                Class<? extends Annotation> nestedAnnotationType = (Class<? extends Annotation>) returnType;
                if (isSynthesizable(nestedAnnotationType)) {
                    synthesizable = Boolean.TRUE;
                    break;
                }
            }
            synthesizableCache.put(annotationType, synthesizable);
            return synthesizable;
        }


        return false;
    }

    static List<String> getAttributeAliasNames(Method attribute) {
        AliasDescriptor descriptor = AliasDescriptor.from(attribute);
        return descriptor != null ? descriptor.getAttributeAliasNames() : Collections.emptyList();
    }

    public static <A extends Annotation> A synthesizeAnnotation(
            A annotation, AnnotatedElement annotatedElement
    ) {
        return synthesizeAnnotation(annotation, (Object) annotatedElement);
    }

    private static AnnotationAttributes getAnnotationAttributes(Object annotatedElement,
                                                                Annotation annotation, boolean classValuesAsString, boolean nestedAnnotationsAsMap) {
        AnnotationAttributes attributes =
                retrieveAnnotationAttributes(annotatedElement, annotation, classValuesAsString, nestedAnnotationsAsMap);
        postProcessAnnotationAttributes(annotatedElement, attributes, classValuesAsString, nestedAnnotationsAsMap);
        return attributes;
    }

    static Map<String, List<String>> getAttributeAliasMap(Class<? extends Annotation> annotationType) {
        if (annotationType == null) {
            return Collections.emptyMap();
        }
        Map<String, List<String>> map = attributeAliasesCache.get(annotationType);
        if (map != null) {
            return map;
        }
        map = new LinkedHashMap<>();
        for (Method attribute : getAttributeMethods(annotationType)) {
            List<String> aliasNames = getAttributeAliasNames(attribute);
            if (!aliasNames.isEmpty()) {
                map.put(attribute.getName(), aliasNames);
            }
        }
        attributeAliasesCache.put(annotationType, map);
        return map;
    }

    public static boolean isAnnotationDeclaredLocally(Class<? extends Annotation> annotationType,
                                                      Class<?> clazz) {
        try {
            return clazz.getDeclaredAnnotation(annotationType) != null;
        } catch (Throwable ex) {
            return false;
        }
    }

    private static final class AliasDescriptor {
        private final Method sourceAttribute;

        private final Class<? extends Annotation> sourceAnnotationType;

        private final String sourceAttributeName;

        private final Method aliasedAttribute;

        private final Class<? extends Annotation> aliasedAnnotationType;

        private final String aliasedAttributeName;

        private final boolean isAliasPair;

        public static AliasDescriptor from(Method attribute) {
            AliasDescriptor descriptor = aliasDescriptorCache.get(attribute);
            if (descriptor != null) {
                return descriptor;
            }
            AliasFor aliasFor = attribute.getAnnotation(AliasFor.class);
            if (aliasFor == null) {
                return null;
            }
            descriptor = new AliasDescriptor(attribute, aliasFor);
            descriptor.validate();
            aliasDescriptorCache.put(attribute, descriptor);
            return descriptor;
        }

        @SuppressWarnings("unchecked")
        private AliasDescriptor(Method sourceAttribute, AliasFor aliasFor) {
            Class<?> declaringClass = sourceAttribute.getDeclaringClass();
            this.sourceAttribute = sourceAttribute;
            this.sourceAnnotationType = (Class<? extends Annotation>) declaringClass;
            this.sourceAttributeName = sourceAttribute.getName();
            this.aliasedAnnotationType = Annotation.class == aliasFor.annotation() ?
                    this.sourceAnnotationType : aliasFor.annotation();

            this.aliasedAttributeName = getAliasedAttributeName(aliasFor, sourceAttribute);

            if (this.aliasedAnnotationType == this.sourceAnnotationType &&
                    this.aliasedAttributeName.equals(this.sourceAttributeName)) {
                throw new AnnotationConfigurationException("Specify 'annotation' to point to a same-named attribute on a meta-annotation");
            }

            try {
                this.aliasedAttribute = this.aliasedAnnotationType.getDeclaredMethod(this.aliasedAttributeName);
            } catch (NoSuchMethodException ex) {
                String errorMsg = String.format("Attribute '%s' annotation [%s] is declared as an @AliasFor nonexistent attribute '%s' in annotation [%s].",
                        this.sourceAttributeName, this.sourceAnnotationType.getName(), this.aliasedAttributeName, this.aliasedAnnotationType.getName());
                throw new AnnotationConfigurationException(errorMsg, ex);
            }

            this.isAliasPair = this.sourceAnnotationType == this.aliasedAnnotationType;
        }

        private void validate() {
            // todo
        }

        public String getAttributeOverrideName(Class<? extends Annotation> metaAnnotationType) {
            for (AliasDescriptor desc = this; desc != null; desc = desc.getAttributeOverrideDescriptor()) {
                if (desc.isOverrideFor(metaAnnotationType)) {
                    return desc.aliasedAttributeName;
                }
            }

            return null;
        }

        private boolean isOverrideFor(Class<? extends Annotation> metaAnnotationType) {
            return this.aliasedAnnotationType == metaAnnotationType;
        }

        private AliasDescriptor getAttributeOverrideDescriptor() {
            if (this.isAliasPair) {
                return null;
            }
            return AliasDescriptor.from(this.aliasedAttribute);
        }

        private String getAliasedAttributeName(AliasFor aliasFor, Method attribute) {
            String attributeName = aliasFor.attribute();
            String value = aliasFor.value();
            boolean attributeDeclared = StringUtils.hasText(attributeName);
            boolean valueDeclared = StringUtils.hasText(value);
            if (attributeDeclared && valueDeclared) {
                throw new AnnotationConfigurationException("Can not declared two attribute, ['value' or 'attribute']");
            }
            attributeName = attributeDeclared ? attributeName : value;
            return StringUtils.hasText(attributeName) ? attributeName.trim() : attribute.getName();
        }

        public List<String> getAttributeAliasNames() {
            if (this.isAliasPair) {
                return Collections.singletonList(this.aliasedAttributeName);
            }
            List<String> aliases = new ArrayList<>();
            for (AliasDescriptor otherDescriptor : getOtherDescriptors()) {
                if (this.isAliasFor(otherDescriptor)) {
                    aliases.add(otherDescriptor.sourceAttributeName);
                }
            }
            return aliases;
        }

        private List<AliasDescriptor> getOtherDescriptors() {
            List<AliasDescriptor> otherDescriptors = new ArrayList<>();
            for (Method currentAttribute : getAttributeMethods(this.sourceAnnotationType)) {
                if (!this.sourceAttribute.equals(currentAttribute)) {
                    AliasDescriptor otherDescriptor = AliasDescriptor.from(currentAttribute);
                    if (otherDescriptor != null) {
                        otherDescriptors.add(otherDescriptor);
                    }
                }
            }
            return otherDescriptors;
        }

        private boolean isAliasFor(AliasDescriptor otherDescriptor) {
            for (AliasDescriptor lhs = this; lhs != null; lhs = lhs.getAttributeOverrideDescriptor()) {
                for (AliasDescriptor rhs = otherDescriptor; rhs != null; rhs = rhs.getAttributeOverrideDescriptor()) {
                    if (lhs.aliasedAttribute.equals(rhs.aliasedAttribute)) {
                        return true;
                    }
                }
            }
            return false;
        }


    }

    private static class DefaultValueHolder {
        final Object defaultValue;

        public DefaultValueHolder(Object defaultValue) {
            this.defaultValue = defaultValue;
        }
    }
}

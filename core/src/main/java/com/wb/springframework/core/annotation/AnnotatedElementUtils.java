package com.wb.springframework.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.*;

import static com.wb.springframework.core.annotation.AnnotationUtils.VALUE;
import static com.wb.springframework.core.annotation.AnnotationUtils.hasPlainJavaAnnotationsOnly;

/**
 * @author WangBing
 * @date 2023/6/17 22:25
 */
public abstract class AnnotatedElementUtils {

    private static final Processor<Boolean> alwaysTrueAnnotationProcessor = new AlwaysTrueBooleanAnnotationProcessor();

    private static final Annotation[] EMPTY_ANNOTATION_ARRAY = new Annotation[0];

    /**
     * 判断某个类是否被特定的注解所标注，AnnotatedElement是Class的父类
     *
     * @param element        class类
     * @param annotationName 注解的名称
     * @return 是否被标注
     */
    public static boolean isAnnotated(AnnotatedElement element, String annotationName) {
        return Boolean.TRUE.equals(searchWithGetSemantics(element, null, annotationName, alwaysTrueAnnotationProcessor));
    }

    // semantics: 含义
    private static <T> T searchWithGetSemantics(AnnotatedElement element, Class<? extends Annotation> annotationType,
                                                String annotationName,
                                                Processor<T> processor) {
        return searchWithGetSemantics(element,
                (annotationType != null ? Collections.singleton(annotationType) : Collections.emptySet()),
                annotationName, null, processor);
    }


    private static <T> T searchWithGetSemantics(AnnotatedElement element, Set<Class<? extends Annotation>> annotationTypes,
                                                String annotationName,
                                                Class<? extends Annotation> containerType,
                                                Processor<T> processor) {
        try {
            return searchWithGetSemantics(element, annotationTypes, annotationName,
                    containerType, processor, new HashSet<>(), 0);
        } catch (Throwable ex) {
            throw new IllegalStateException("Failed to introspect annotations on " + element, ex);
        }
    }

    private static <T> T searchWithGetSemantics(AnnotatedElement element,
                                                Set<Class<? extends Annotation>> annotationTypes,
                                                String annotationName,
                                                Class<? extends Annotation> containerType,
                                                Processor<T> processor,
                                                Set<AnnotatedElement> visited, int metaDepth) {
        if (visited.add(element)) {
            try {
                List<Annotation> declaredAnnotations = Arrays.asList(AnnotationUtils.getDeclaredAnnotations(element));
                T result = searchWithGetSemanticsInAnnotations(element, declaredAnnotations,
                        annotationTypes, annotationName, containerType, processor, visited, metaDepth);
                if (result != null) {
                    return result;
                }
                if (element instanceof Class) {
                    Class<?> superClass = ((Class<?>) element).getSuperclass();
                    if (superClass != null && superClass != Object.class) {
                        List<Annotation> inheritedAnnotations = new LinkedList<>();
                        for (Annotation annotation : element.getAnnotations()) {
                            if (!declaredAnnotations.contains(annotation)) {
                                inheritedAnnotations.add(annotation);
                            }
                        }
                        result = searchWithGetSemanticsInAnnotations(element, inheritedAnnotations,
                                annotationTypes, annotationName, containerType, processor, visited, metaDepth);
                        if (result != null) {
                            return result;
                        }
                    }
                }
            } catch (Throwable ex) {
                ex.printStackTrace();
                // no op
            }
        }
        return null;
    }

    private static <T> T searchWithGetSemanticsInAnnotations(AnnotatedElement element,
                                                             List<Annotation> annotations, Set<Class<? extends Annotation>> annotationTypes,
                                                             String annotationName, Class<? extends Annotation> containerType,
                                                             Processor<T> processor, Set<AnnotatedElement> visited, int metaDepth) {
        for (Annotation annotation : annotations) {
            Class<? extends Annotation> currentAnnotationType = annotation.annotationType();
            // 如果当前的注解不是java.lang.annotation包下面的注解
            if (!AnnotationUtils.isInJavaLangAnnotationPackage(currentAnnotationType)) {
                if (annotationTypes.contains(currentAnnotationType) ||
                        currentAnnotationType.getName().equals(annotationName) ||
                        processor.alwaysProcesses()) {
                    // 默认返回TRUE
                    T result = processor.process(element, annotation, metaDepth);
                    if (result != null) {
                        if (processor.aggregates() && metaDepth == 0) {
                            processor.getAggregatedResults().add(result);
                        }
                        return result;
                    }
                } else if (currentAnnotationType == containerType) {
                    for (Annotation contained : getRawAnnotationsFromContainer(element, annotation)) {
                        T result = processor.process(element, contained, metaDepth);
                        if (result != null) {
                            processor.getAggregatedResults().add(result);
                        }
                    }
                }
            }
        }

        for (Annotation annotation : annotations) {
            Class<? extends Annotation> currentAnnotationType = annotation.annotationType();
            if (!hasPlainJavaAnnotationsOnly(currentAnnotationType)) {
                T result = searchWithGetSemantics(currentAnnotationType, annotationTypes,
                        annotationName, containerType, processor, visited, metaDepth + 1);
                if (result != null) {
                    processor.postProcess(element, annotation, result);
                    if (processor.aggregates() && metaDepth == 0) {
                        processor.getAggregatedResults().add(result);
                    }
                    return result;
                }
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static <A extends Annotation> A[] getRawAnnotationsFromContainer(AnnotatedElement element,
                                                                             Annotation container) {
        try {
            A[] value = (A[]) AnnotationUtils.getValue(container);
            if (value != null) {
                return value;
            }
        } catch (Throwable ex) {
            // no op
        }
        return (A[]) EMPTY_ANNOTATION_ARRAY;
    }

    public static AnnotationAttributes getMergedAnnotationAttributes(AnnotatedElement element,
                                                                     String annotationName, boolean classValuesAsString, boolean nestedAnnotationsAsMap) {
        AnnotationAttributes attributes = searchWithGetSemantics(element, null, annotationName,
                new MergedAnnotationAttributesProcessor(classValuesAsString, nestedAnnotationsAsMap));
        AnnotationUtils.postProcessAnnotationAttributes(element, attributes, classValuesAsString, nestedAnnotationsAsMap);
        return attributes;
    }

    private static class MergedAnnotationAttributesProcessor implements Processor<AnnotationAttributes> {

        private final boolean classValuesAsString;

        private final boolean nestedAnnotationAsMap;

        private final boolean aggregates;

        private final List<AnnotationAttributes> aggregatedResults;

        MergedAnnotationAttributesProcessor() {
            this(false, false, false);
        }

        MergedAnnotationAttributesProcessor(boolean classValuesAsString, boolean nestedAnnotationAsMap) {
            this(classValuesAsString, nestedAnnotationAsMap, false);
        }

        MergedAnnotationAttributesProcessor(boolean classValuesAsString, boolean nestedAnnotationAsMap,
                                            boolean aggregates) {
            this.classValuesAsString = classValuesAsString;
            this.nestedAnnotationAsMap = nestedAnnotationAsMap;
            this.aggregates = aggregates;
            this.aggregatedResults = aggregates ? new ArrayList<>() : Collections.emptyList();
        }

        @Override
        public AnnotationAttributes process(AnnotatedElement annotatedElement, Annotation annotation, int metaDepth) {
            return AnnotationUtils.retrieveAnnotationAttributes(annotatedElement, annotation,
                    this.classValuesAsString, this.nestedAnnotationAsMap);
        }

        @Override
        public void postProcess(AnnotatedElement element, Annotation annotation, AnnotationAttributes attributes) {
            annotation = AnnotationUtils.synthesizeAnnotation(annotation, element);
            Class<? extends Annotation> targetAnnotationType = attributes.annotationType();

            Set<String> valuesAlreadyReplaced = new HashSet<>();
            for (Method attributeMethod : AnnotationUtils.getAttributeMethods(annotation.annotationType())) {
                String attributeName = attributeMethod.getName();
                String attributeOverrideName = AnnotationUtils.getAttributeOverrideName(attributeMethod, targetAnnotationType);

                if (attributeOverrideName != null) {
                    if (valuesAlreadyReplaced.contains(attributeOverrideName)) {
                        continue;
                    }
                    List<String> targetAttributeNames = new ArrayList<>();
                    targetAttributeNames.add(attributeOverrideName);
                    valuesAlreadyReplaced.add(attributeOverrideName);

                    List<String> aliases = AnnotationUtils.getAttributeAliasMap(targetAnnotationType).get(attributeOverrideName);
                    if (aliases != null) {
                        for (String alias : aliases) {
                            if (!valuesAlreadyReplaced.contains(alias)) {
                                targetAttributeNames.add(alias);
                                valuesAlreadyReplaced.add(alias);
                            }
                        }
                    }
                    overrideAttributes(element, annotation, attributes, attributeName, targetAttributeNames);
                } else if(!VALUE.equals(attributeName) && attributes.containsKey(attributeName)) {
                    overrideAttributes(element, annotation, attributes, attributeName, attributeName);
                }
            }
        }

        private void overrideAttributes(AnnotatedElement element, Annotation annotation,
                                        AnnotationAttributes attributes, String sourceAttributeName,
                                        List<String> targetAttributeNames) {
            Object adaptedValue = getAdaptedValue(element, annotation, sourceAttributeName);

            for (String targetAttributeName : targetAttributeNames) {
                attributes.put(targetAttributeName, adaptedValue);
            }
        }

        private void overrideAttributes(AnnotatedElement element, Annotation annotation,
                                        AnnotationAttributes attributes, String sourceAttributeName,
                                        String targetAttributeName) {
            attributes.put(targetAttributeName, getAdaptedValue(element, annotation, sourceAttributeName));
        }

        private Object getAdaptedValue(AnnotatedElement element, Annotation annotation, String sourceAttributeName) {
            Object value = AnnotationUtils.getValue(annotation, sourceAttributeName);
            return AnnotationUtils.adaptValue(element, value, this.classValuesAsString, this.nestedAnnotationAsMap);
        }

        @Override
        public boolean alwaysProcesses() {
            return false;
        }

        @Override
        public boolean aggregates() {
            return this.aggregates;
        }

        @Override
        public List<AnnotationAttributes> getAggregatedResults() {
            return this.aggregatedResults;
        }
    }


    private interface Processor<T> {

        T process(AnnotatedElement annotatedElement, Annotation annotation, int metaDepth);

        void postProcess(AnnotatedElement annotatedElement, Annotation annotation, T result);

        boolean alwaysProcesses();

        boolean aggregates();

        List<T> getAggregatedResults();
    }

    private abstract static class SimpleAnnotationProcessor<T> implements Processor<T> {
        private final boolean alwaysProcesses;

        public SimpleAnnotationProcessor() {
            this(false);
        }

        public SimpleAnnotationProcessor(boolean alwaysProcesses) {
            this.alwaysProcesses = alwaysProcesses;
        }

        @Override
        public final boolean alwaysProcesses() {
            return this.alwaysProcesses;
        }

        @Override
        public final void postProcess(AnnotatedElement annotatedElement, Annotation annotation, T result) {
            // no op
        }

        @Override
        public final boolean aggregates() {
            return false;
        }

        @Override
        public final List<T> getAggregatedResults() {
            throw new UnsupportedOperationException("SimpleAnnotationProcessor does not support aggregated results.");
        }
    }

    static class AlwaysTrueBooleanAnnotationProcessor extends SimpleAnnotationProcessor<Boolean> {
        @Override
        public final Boolean process(AnnotatedElement annotatedElement, Annotation annotation, int metaDepth) {
            return Boolean.TRUE;
        }
    }
}

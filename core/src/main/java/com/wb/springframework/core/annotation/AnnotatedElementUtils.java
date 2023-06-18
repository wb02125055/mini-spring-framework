package com.wb.springframework.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author WangBing
 * @date 2023/6/17 22:25
 */
public abstract class AnnotatedElementUtils {

    private static final Processor<Boolean> alwaysTrueAnnotationProcessor = new AlwaysTrueBooleanAnnotationProcessor();

    /**
     * 判断某个类是否被特定的注解所标注，AnnotatedElement是Class的父类
     * @param element class类
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
        return null;
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

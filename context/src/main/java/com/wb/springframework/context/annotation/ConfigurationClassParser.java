package com.wb.springframework.context.annotation;

import com.wb.springframework.beans.BeanDefinitionStoreException;
import com.wb.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import com.wb.springframework.beans.factory.config.BeanDefinition;
import com.wb.springframework.beans.factory.config.BeanDefinitionHolder;
import com.wb.springframework.beans.factory.support.AbstractBeanDefinition;
import com.wb.springframework.beans.factory.support.BeanDefinitionRegistry;
import com.wb.springframework.beans.factory.support.BeanNameGenerator;
import com.wb.springframework.core.Ordered;
import com.wb.springframework.core.annotation.AnnotationAttributes;
import com.wb.springframework.core.annotation.AnnotationUtils;
import com.wb.springframework.core.env.Environment;
import com.wb.springframework.core.io.ResourceLoader;
import com.wb.springframework.core.type.AnnotationMetadata;
import com.wb.springframework.core.type.StandardAnnotationMetadata;
import com.wb.springframework.core.type.classreading.MetadataReader;
import com.wb.springframework.core.type.classreading.MetadataReaderFactory;
import com.wb.springframework.stereotype.Component;
import com.wb.springframework.util.ClassUtils;
import com.wb.springframework.util.CollectionUtils;
import com.wb.springframework.util.LinkedMultiValueMap;
import com.wb.springframework.util.MultiValueMap;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.*;

/**
 * @author WangBing
 * @date 2023/6/20 07:42
 */
class ConfigurationClassParser {

    private final Map<ConfigurationClass, ConfigurationClass> configurationClasses = new LinkedHashMap<>();

    private final Environment environment;

    private final ResourceLoader resourceLoader;

    private final BeanDefinitionRegistry registry;

    private final ComponentScanAnnotationParser componentScanParser;

    private final MetadataReaderFactory metadataReaderFactory;

    private final ImportStack importStack = new ImportStack();

    public ConfigurationClassParser(MetadataReaderFactory metadataReaderFactory,
                                    Environment environment,
                                    ResourceLoader resourceLoader,
                                    BeanNameGenerator componentScanBeanNameGenerator,
                                    BeanDefinitionRegistry registry) {
        this.metadataReaderFactory = metadataReaderFactory;
        this.environment = environment;
        this.resourceLoader = resourceLoader;
        this.registry = registry;
        this.componentScanParser = new ComponentScanAnnotationParser(
                resourceLoader, componentScanBeanNameGenerator, registry
        );
    }

    public void parse(Set<BeanDefinitionHolder> configCandidates) {
        for (BeanDefinitionHolder holder : configCandidates) {
            BeanDefinition bd = holder.getBeanDefinition();
            try {
                if (bd instanceof AnnotatedBeanDefinition) {
                    // no op
                    parse(((AnnotatedBeanDefinition) bd).getMetadata(), holder.getBeanName());
                } else if (bd instanceof AbstractBeanDefinition && ((AbstractBeanDefinition) bd).hasBeanClass()) {
                    parse(((AbstractBeanDefinition) bd).getBeanClass(), holder.getBeanName());
                } else {
                    // no op
                }
            } catch (BeanDefinitionStoreException ex) {
                throw ex;
            } catch (Throwable e) {
                throw new BeanDefinitionStoreException("Failed to parse configuration class ["
                        + bd.getBeanClassName() + "]", e);
            }
        }
    }


    protected final void parse(Class<?> clazz, String beanName) throws IOException {
        processConfigurationClass(new ConfigurationClass(clazz, beanName));
    }

    protected final void parse(AnnotationMetadata metadata, String beanName) throws IOException {
        // 处理配置类
        processConfigurationClass(new ConfigurationClass(metadata, beanName));
    }

    protected void processConfigurationClass(ConfigurationClass configClass) throws IOException {
        // todo : condition evaluator
        ConfigurationClass existingClass = this.configurationClasses.get(configClass);
        if (existingClass != null) {
            // todo: imported
            if (configClass.isImported()) {
                // merge
                return;
            } else {
                this.configurationClasses.remove(configClass);
                // todo
            }
        }

        SourceClass sourceClass = asSourceClass(configClass);
        do {
            sourceClass = doProcessConfigurationClass(configClass, sourceClass);
        } while (sourceClass != null);

        this.configurationClasses.put(configClass, configClass);
    }

    protected final SourceClass doProcessConfigurationClass(ConfigurationClass configClass, SourceClass sourceClass)
            throws IOException {
        if (configClass.getMetadata().isAnnotated(Component.class.getName())) {
            // 处理内部类
            processMemberClasses(configClass, sourceClass);
        }

        // TODO: 处理@PropertySource注解

        // 处理@ComponentScan注解或者@ComponentScans注解，并扫描包下的所有bean，然后转换为填充之后的ConfigurationClass
        Set<AnnotationAttributes> componentScans = AnnotationConfigUtils.attributesForRepeatable(
                sourceClass.getMetadata(), ComponentScans.class, ComponentScan.class
        );
        // todo: 处理是否跳过bean某个bean定义的解析
        if (!componentScans.isEmpty()) {
            for (AnnotationAttributes componentScan : componentScans) {
                // 解析@ComponentScan和@ComonentScans配置的包扫描路径中所包含的类，例如：basePackages="com.wb.spring"。然后会扫描这个包及其子包下面的
                //   所有class，并将其解析为BeanDefinition
                Set<BeanDefinitionHolder> scannedBeanDefinitions =
                        this.componentScanParser.parse(componentScan, sourceClass.getMetadata().getClassName());
                // TODO: 下一步处理
            }
        }
        return null;
    }

    private void processMemberClasses(ConfigurationClass configClass, SourceClass sourceClass) throws IOException {
        Collection<SourceClass> memberClasses = sourceClass.getMemberClasses();
        if (!memberClasses.isEmpty()) {
            List<SourceClass> candidates = new ArrayList<>(memberClasses.size());
            for (SourceClass memberClass : memberClasses) {
                if (ConfigurationClassUtils.isConfigurationCandidate(memberClass.getMetadata()) &&
                        !memberClass.getMetadata().getClassName().equals(configClass.getMetadata().getClassName())) {
                    candidates.add(memberClass);
                }
            }
            // TODO: 排序
            for (SourceClass candidate : candidates) {
                if (this.importStack.contains(configClass)) {
                    // TODO 处理错误日志
                } else {
                    // 先压栈，将最外层的配置类压栈处理
                    this.importStack.push(configClass);
                    try {
                        // 递归处理当前已经压入栈的配置类
                        processConfigurationClass(candidate.asConfigClass(configClass));
                    } finally {
                        // 处理完成一个类，然后执行出栈操作。最后面出栈的是最外层的配置类
                        this.importStack.pop();
                    }
                }
            }
        }
    }


    private SourceClass asSourceClass(ConfigurationClass configurationClass) throws IOException {
        AnnotationMetadata metadata = configurationClass.getMetadata();
        if (metadata instanceof StandardAnnotationMetadata) {
            return asSourceClass(((StandardAnnotationMetadata) metadata).getIntrospectedClass());
        }
        return asSourceClass(metadata.getClassName());
    }

    SourceClass asSourceClass(Class<?> classType) throws IOException {
        if (classType == null) {
            return new SourceClass(Object.class);
        }
        try {
            for (Annotation ann : classType.getAnnotations()) {
                AnnotationUtils.validateAnnotation(ann);
            }
            return new SourceClass(classType);
        } catch (Throwable ex) {
            return asSourceClass(classType.getName());
        }
    }

    SourceClass asSourceClass(String className) throws IOException {
        if (className == null) {
            return new SourceClass(Object.class);
        }
        if (className.startsWith("java")) {
            try {
                return new SourceClass(ClassUtils.forName(className, this.resourceLoader.getClassLoader()));
            } catch (ClassNotFoundException ex) {
                throw new IOException("Failed to load class [" + className + "]", ex);
            }
        }
        return new SourceClass(this.metadataReaderFactory.getMetadataReader(className));
    }


    private class SourceClass implements Ordered {
        private final Object source;
        private final AnnotationMetadata metadata;

        public SourceClass(Object source) {
            this.source = source;
            if (source instanceof Class) {
                this.metadata = new StandardAnnotationMetadata((Class<?>) source, true);
            } else {
                this.metadata = ((MetadataReader) source).getAnnotationMetadata();
            }
        }

        public AnnotationMetadata getMetadata() {
            return metadata;
        }

        public ConfigurationClass asConfigClass(ConfigurationClass importedBy) {
            if (this.source instanceof Class) {
                return new ConfigurationClass((Class<?>) this.source, importedBy);
            }
            return new ConfigurationClass((MetadataReader) this.source, importedBy);
        }

        public Collection<SourceClass> getMemberClasses() throws IOException {
            Object sourceToProcess = this.source;
            if (sourceToProcess instanceof Class) {
                Class<?> sourceClass = (Class<?>) sourceToProcess;
                try {
                    Class<?>[] declaredClasses = sourceClass.getDeclaredClasses();
                    List<SourceClass> members = new ArrayList<>(declaredClasses.length);
                    for (Class<?> declaredClass : declaredClasses) {
                        members.add(asSourceClass(declaredClass));
                    }
                    return members;
                } catch (NoClassDefFoundError err) {
                    // fall back to ASM below
                    sourceToProcess = metadataReaderFactory.getMetadataReader(sourceClass.getName());
                }
            }

            // ASM-based resolution safe for non-resolvable classes as well
            MetadataReader sourceReader = (MetadataReader) sourceToProcess;
            String[] memberClassNames = sourceReader.getClassMetadata().getMemberClassNames();
            List<SourceClass> members = new ArrayList<>(memberClassNames.length);
            for (String memberClassName : memberClassNames) {
                try {
                    members.add(asSourceClass(memberClassName));
                } catch (IOException ex) {
                    // debug log
                }
            }
            return members;
        }

        @Override
        public int getOrder() {
            return 0;
        }
    }


    private static class ImportStack extends ArrayDeque<ConfigurationClass> implements ImportRegistry {

        private final MultiValueMap<String, AnnotationMetadata> imports = new LinkedMultiValueMap<>();

        public void registerImport(AnnotationMetadata importingClass, String importedClass) {
            this.imports.add(importedClass, importingClass);
        }

        @Override
        public AnnotationMetadata getImportingClassFor(String importedClass) {
            return CollectionUtils.lastElement(this.imports.get(importedClass));
        }

        @Override
        public void removeImportingClass(String importingClass) {
            for (List<AnnotationMetadata> list : this.imports.values()) {
                for (Iterator<AnnotationMetadata> iterator = list.iterator(); iterator.hasNext(); ) {
                    if (iterator.next().getClassName().equals(importingClass)) {
                        iterator.remove();
                        break;
                    }
                }
            }
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder("[");
            Iterator<ConfigurationClass> iterator = iterator();
            while (iterator.hasNext()) {
                builder.append(iterator.next().getSimpleName());
                if (iterator.hasNext()) {
                    builder.append("->");
                }
            }
            return builder.append("]").toString();
        }
    }


}

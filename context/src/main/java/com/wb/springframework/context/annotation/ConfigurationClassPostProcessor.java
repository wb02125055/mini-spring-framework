package com.wb.springframework.context.annotation;

import com.wb.springframework.beans.BeansException;
import com.wb.springframework.beans.factory.config.BeanDefinition;
import com.wb.springframework.beans.factory.config.BeanDefinitionHolder;
import com.wb.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import com.wb.springframework.beans.factory.support.BeanDefinitionRegistry;
import com.wb.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import com.wb.springframework.beans.factory.support.BeanNameGenerator;
import com.wb.springframework.core.Ordered;
import com.wb.springframework.core.PriorityOrdered;
import com.wb.springframework.core.env.Environment;
import com.wb.springframework.core.env.StandardEnvironment;
import com.wb.springframework.core.io.DefaultResourceLoader;
import com.wb.springframework.core.io.ResourceLoader;
import com.wb.springframework.core.type.classreading.CachingMetadataReaderFactory;
import com.wb.springframework.core.type.classreading.MetadataReaderFactory;

import java.util.*;

/**
 * @author WangBing
 * @date 2023/5/21 20:37
 */
public class ConfigurationClassPostProcessor implements BeanDefinitionRegistryPostProcessor, PriorityOrdered {

    private MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory();

    private final Set<Integer> registriesPostProcessed = new HashSet<>();

    private final Set<Integer> factoriesPostProcessed = new HashSet<>();

    private Environment environment;

    private ResourceLoader resourceLoader = new DefaultResourceLoader();

    private BeanNameGenerator componentScanBeanNameGenerator = new AnnotationBeanNameGenerator();


    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        int registryId = System.identityHashCode(registry);
        if (this.registriesPostProcessed.contains(registryId)) {
            throw new IllegalStateException("postProcessBeanDefinitionRegistry already called on this post-processor against " + registry);
        }
        if (this.factoriesPostProcessed.contains(registryId)) {
            throw new IllegalStateException("postProcessBeanFactory already called on this post-processor against " + registry);
        }
        // 将即将要处理的registry放入到set集合中，防止重复执行
        this.registriesPostProcessed.add(registryId);

        // 解析配置类中的bean定义
        processConfigBeanDefinitions(registry);
    }

    public void processConfigBeanDefinitions(BeanDefinitionRegistry registry) {
        // 解析配置类中的bean定义
        List<BeanDefinitionHolder> configCandidates = new ArrayList<>();

        // 获取bean定义注册中心的所有bean的名称列表
        String[] candidateNames = registry.getBeanDefinitionNames();

        // 遍历所有的BeanDefinition的名称，筛选出带有注解的BeanDefinition
        for (String beanName : candidateNames) {
            // 从bean注册中心中根据bean的名称获取bean定义
            BeanDefinition beanDef = registry.getBeanDefinition(beanName);
            if (ConfigurationClassUtils.isFullConfigurationClass(beanDef) ||
                    ConfigurationClassUtils.isLiteConfigurationClass(beanDef)) {
                // log
            } else if (ConfigurationClassUtils.checkConfigurationClassCandidate(beanDef, this.metadataReaderFactory)) {
                configCandidates.add(new BeanDefinitionHolder(beanName, beanDef));
            }
        }

        if (configCandidates.isEmpty()) {
            return;
        }

        // TODO: 排序
        configCandidates.sort((bd1, bd2) -> {
            return -1;
        });

        if (this.environment == null) {
            environment = new StandardEnvironment();
        }

        ConfigurationClassParser parser = new ConfigurationClassParser(
                this.metadataReaderFactory, this.environment, this.resourceLoader,
                this.componentScanBeanNameGenerator, registry
        );

        Set<BeanDefinitionHolder> candidates = new LinkedHashSet<>(configCandidates);
        Set<ConfigurationClass> alreadyParsed = new HashSet<>(configCandidates.size());


        do {
            parser.parse(candidates);

        } while (!candidates.isEmpty());


    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}

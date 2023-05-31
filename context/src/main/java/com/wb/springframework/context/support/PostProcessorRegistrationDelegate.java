package com.wb.springframework.context.support;

import com.wb.springframework.beans.factory.config.BeanFactoryPostProcessor;
import com.wb.springframework.beans.factory.config.ConfigurableBeanFactory;
import com.wb.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import com.wb.springframework.beans.factory.support.BeanDefinitionRegistry;
import com.wb.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import com.wb.springframework.beans.factory.support.DefaultListableBeanFactory;
import com.wb.springframework.core.OrderComparator;
import com.wb.springframework.core.PriorityOrdered;

import java.util.*;

/**
 * @author WangBing
 * @date 2023/5/21 20:12
 */
final class PostProcessorRegistrationDelegate {

    public static void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory,
                                                       List<BeanFactoryPostProcessor> beanFactoryPostProcessors) {

        Set<String> processedBeans = new HashSet<>();

        if (beanFactory instanceof BeanDefinitionRegistry) {

            BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;

            List<BeanFactoryPostProcessor> regularPostProcessors = new ArrayList<>();

            List<BeanDefinitionRegistryPostProcessor> registryProcessors = new ArrayList<>();

            for (BeanFactoryPostProcessor postProcessor : beanFactoryPostProcessors) {
                if (postProcessor instanceof BeanDefinitionRegistryPostProcessor) {
                    BeanDefinitionRegistryPostProcessor registryPostProcessor = (BeanDefinitionRegistryPostProcessor) postProcessor;
                    registryPostProcessor.postProcessBeanDefinitionRegistry(registry);
                    registryProcessors.add(registryPostProcessor);
                } else {
                    regularPostProcessors.add(postProcessor);
                }
            }

            List<BeanDefinitionRegistryPostProcessor> currentRegistryProcessors = new ArrayList<>();

            String[] postProcessorNames =
                    beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
            for (String ppName : postProcessorNames) {
                if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
                    currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
                    processedBeans.add(ppName);
                }
            }
            sortPostProcessors(currentRegistryProcessors, beanFactory);
            registryProcessors.addAll(currentRegistryProcessors);
        }
    }

    private static void sortPostProcessors(List<?> postProcessors, ConfigurableListableBeanFactory beanFactory) {
        Comparator<Object> comparatorToUse = null;
        if (beanFactory instanceof DefaultListableBeanFactory) {
            comparatorToUse = ((DefaultListableBeanFactory) beanFactory).getDependencyComparator();
        }
        if (comparatorToUse == null) {
            comparatorToUse = OrderComparator.INSTANCE;
        }
        postProcessors.sort(comparatorToUse);
    }
}

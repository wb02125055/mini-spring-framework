package com.wb.springframework.beans.factory.config;

import com.wb.springframework.beans.BeanMetadataElement;

/**
 * @author WangBing
 * @date 2023/5/28 22:10
 */
public interface BeanReference extends BeanMetadataElement {
    String getBeanName();
}

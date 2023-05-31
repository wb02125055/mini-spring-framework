package com.wb.springframework.beans;

/**
 * @author WangBing
 * @date 2023/5/7 10:04
 */
public interface BeanMetadataElement {
    /**
     * 获取源对象，如果不存在则返回null
     * @return source
     */
    Object getSource();
}

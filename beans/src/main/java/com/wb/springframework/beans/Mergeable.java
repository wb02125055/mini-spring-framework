package com.wb.springframework.beans;

/**
 * @author WangBing
 * @date 2023/5/9 22:16
 */
public interface Mergeable {
    boolean isMergeEnabled();

    Object merge(Object parent);
}

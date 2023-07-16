package com.wb.springframework.util;

import java.util.List;
import java.util.Map;

/**
 * @author WangBing
 * @date 2023/7/1 11:43
 */
public interface MultiValueMap<K, V> extends Map<K, List<V>> {
    V getFirst(K key);

    void add(K key, V value);

    void addAll(K key, List<? extends V> values);

    void addAll(MultiValueMap<K, V> values);

    void set(K key, V value);

    void setAll(Map<K, V> values);

    Map<K, V> toSingleValueMap();
}

package com.wb.springframework.util;

import java.util.Collection;
import java.util.List;

/**
 * @author WangBing
 * @date 2023/7/1 13:29
 */
public abstract class CollectionUtils {

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static <T> T lastElement(List<T> list) {
        if (isEmpty(list)) {
            return null;
        }
        return list.get(list.size() - 1);
    }

}

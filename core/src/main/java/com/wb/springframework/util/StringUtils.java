package com.wb.springframework.util;

import java.util.Collection;
import java.util.List;

/**
 * @author WangBing
 * @date 2023/5/14 21:36
 */
public abstract class StringUtils {
    public static String arrayToDelimitedString(Object[] arr, String delimiter) {
        if (ObjectUtils.isEmpty(arr)) {
            return "";
        }
        if (arr.length == 1) {
            return ObjectUtils.nullSafeToString(arr[0]);
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            if (i > 0) {
                sb.append(delimiter);
            }
            sb.append(arr[i]);
        }
        return sb.toString();
    }

    public static boolean hasText(String str) {
        return str != null && !str.isEmpty() && containsText(str);
    }

    private static boolean containsText(CharSequence str) {
        int len = str.length();
        for (int i = 0 ; i < len; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    public static String[] toStringArray(Collection<String> collection) {
        return null != collection ? collection.toArray(new String[0]) : new String[0];
    }
}

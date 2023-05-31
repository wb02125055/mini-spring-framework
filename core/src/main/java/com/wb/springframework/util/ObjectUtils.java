package com.wb.springframework.util;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author WangBing
 * @date 2023/5/7 10:50
 */
public abstract class ObjectUtils {

    private static final int INITIAL_HASH = 7;
    private static final int MULTIPLIER = 31;

    private static final String NULL_STRING = "null";

    private static final String EMPTY_STRING = "";

    private static final String ARRAY_START = "{";
    private static final String ARRAY_END = "}";
    private static final String EMPTY_ARRAY = ARRAY_START + ARRAY_END;

    private static final String ARRAY_ELEMENT_SEPARATOR = ", ";

    public static boolean nullSafeEquals(Object obj1, Object obj2) {
        if (obj1 == obj2) {
            return true;
        }
        if (obj1 == null || obj2 == null) {
            return false;
        }
        if (obj1.equals(obj2)) {
            return true;
        }
        if (obj1.getClass().isArray() && obj2.getClass().isArray()) {
            return arrayEquals(obj1, obj2);
        }
        return false;
    }
    private static boolean arrayEquals(Object obj1, Object obj2) {
        if (obj1 instanceof Object[] && obj2 instanceof Object[]) {
            return Arrays.equals((Object[]) obj1, (Object[]) obj2);
        }
        if (obj1 instanceof boolean[] && obj2 instanceof boolean[]) {
            return Arrays.equals((boolean[]) obj1, (boolean[]) obj2);
        }
        if (obj1 instanceof byte[] && obj2 instanceof byte[]) {
            return Arrays.equals((byte[]) obj1, (byte[]) obj2);
        }
        if (obj1 instanceof char[] && obj2 instanceof char[]) {
            return Arrays.equals((char[]) obj1, (char[]) obj2);
        }
        if (obj1 instanceof double[] && obj2 instanceof double[]) {
            return Arrays.equals((double[]) obj1, (double[]) obj2);
        }
        if (obj1 instanceof float[] && obj2 instanceof float[]) {
            return Arrays.equals((float[]) obj1, (float[]) obj2);
        }
        if (obj1 instanceof int[] && obj2 instanceof int[]) {
            return Arrays.equals((int[]) obj1, (int[]) obj2);
        }
        if (obj1 instanceof long[] && obj2 instanceof long[]) {
            return Arrays.equals((long[]) obj1, (long[]) obj2);
        }
        if (obj1 instanceof short[] && obj2 instanceof short[]) {
            return Arrays.equals((short[]) obj1, (short[]) obj2);
        }
        return false;
    }

    public static int nullSafeHashCode(Object obj) {
        if (null == obj) {
            return 0;
        }
        if (obj.getClass().isArray()) {
            if (obj instanceof Object[]) {
                return nullSafeHashCode((Object[]) obj);
            }
            if (obj instanceof boolean[]) {
                return nullSafeHashCode((boolean[]) obj);
            }
            if (obj instanceof byte[]) {
                return nullSafeHashCode((byte[]) obj);
            }
            if (obj instanceof char[]) {
                return nullSafeHashCode((char[]) obj);
            }
            if (obj instanceof double[]) {
                return nullSafeHashCode((double[]) obj);
            }
            if (obj instanceof float[]) {
                return nullSafeHashCode((float[]) obj);
            }
            if (obj instanceof int[]) {
                return nullSafeHashCode((int[]) obj);
            }
            if (obj instanceof long[]) {
                return nullSafeHashCode((long[]) obj);
            }
            if (obj instanceof short[]) {
                return nullSafeHashCode((short[]) obj);
            }
        }
        return obj.hashCode();
    }
    public static int nullSafeHashCode(boolean[] array) {
        if (null == array) {
            return 0;
        }
        int hash = INITIAL_HASH;
        for (boolean element : array) {
            hash = MULTIPLIER * hash + Boolean.hashCode(element);
        }
        return hash;
    }
    public static int nullSafeHashCode(byte[] array) {
        if (null == array) {
            return 0;
        }
        int hash = INITIAL_HASH;
        for (byte element : array) {
            hash = MULTIPLIER * hash + element;
        }
        return hash;
    }
    public static int nullSafeHashCode(char[] array) {
        if (null == array) {
            return 0;
        }
        int hash = INITIAL_HASH;
        for (char element : array) {
            hash = MULTIPLIER * hash + element;
        }
        return hash;
    }
    public static int nullSafeHashCode(double[] array) {
        if (null == array) {
            return 0;
        }
        int hash = INITIAL_HASH;
        for (double element : array) {
            hash = MULTIPLIER * hash + Double.hashCode(element);
        }
        return hash;
    }
    public static int nullSafeHashCode(float[] array) {
        if (null == array) {
            return 0;
        }
        int hash = INITIAL_HASH;
        for (float element : array) {
            hash = MULTIPLIER * hash + Float.hashCode(element);
        }
        return hash;
    }
    public static int nullSafeHashCode(int[] array) {
        if (null == array) {
            return 0;
        }
        int hash = INITIAL_HASH;
        for (int element : array) {
            hash = MULTIPLIER * hash + element;
        }
        return hash;
    }
    public static int nullSafeHashCode(long[] array) {
        if (null == array) {
            return 0;
        }
        int hash = INITIAL_HASH;
        for (long element : array) {
            hash = MULTIPLIER * hash + Long.hashCode(element);
        }
        return hash;
    }
    public static int nullSafeHashCode(short[] array) {
        if (null == array) {
            return 0;
        }
        int hash = INITIAL_HASH;
        for (short element : array) {
            hash = MULTIPLIER * hash + element;
        }
        return hash;
    }
    public static int nullSafeHashCode(Object[] array) {
        if (array == null) {
            return 0;
        }
        int hash = INITIAL_HASH;
        for (Object element : array) {
            hash = MULTIPLIER * hash + nullSafeHashCode(element);
        }
        return hash;
    }

    public static boolean isEmpty(Object[] arr) {
        return null == arr || arr.length == 0;
    }

    public static String nullSafeToString(Object obj) {
        if (obj == null) {
            return NULL_STRING;
        }
        if (obj instanceof String) {
            return (String) obj;
        }
        if (obj instanceof Object[]) {
            return nullSafeToString((Object[]) obj);
        }
        if (obj instanceof boolean[]) {
            return nullSafeToString((boolean[]) obj);
        }
        if (obj instanceof byte[]) {
            return nullSafeToString((byte[]) obj);
        }
        if (obj instanceof char[]) {
            return nullSafeToString((char[]) obj);
        }
        if (obj instanceof double[]) {
            return nullSafeToString((double[]) obj);
        }
        if (obj instanceof float[]) {
            return nullSafeToString((float[]) obj);
        }
        if (obj instanceof int[]) {
            return nullSafeToString((int[]) obj);
        }
        if (obj instanceof long[]) {
            return nullSafeToString((long[]) obj);
        }
        if (obj instanceof short[]) {
            return nullSafeToString((short[]) obj);
        }
        String str = obj.toString();
        return (null != str ? str : EMPTY_STRING);
    }
    public static String nullSafeToString(Object[] array) {
        if (null == array) {
            return NULL_STRING;
        }
        int length = array.length;
        if (0 == length) {
            return EMPTY_ARRAY;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0 ; i < length; i++) {
            if (0 == i) {
                sb.append(ARRAY_START);
            } else {
                sb.append(ARRAY_ELEMENT_SEPARATOR);
            }
            sb.append(array[i]);
        }
        sb.append(ARRAY_END);
        return sb.toString();
    }
    public static String nullSafeToString(boolean[] array) {
        if (null == array) {
            return NULL_STRING;
        }
        int length = array.length;
        if (0 == length) {
            return EMPTY_ARRAY;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0 ; i < length; i++) {
            if (0 == i) {
                sb.append(ARRAY_START);
            } else {
                sb.append(ARRAY_ELEMENT_SEPARATOR);
            }
            sb.append(array[i]);
        }
        sb.append(ARRAY_END);
        return sb.toString();
    }
    public static String nullSafeToString(byte[] array) {
        if (null == array) {
            return NULL_STRING;
        }
        int length = array.length;
        if (0 == length) {
            return EMPTY_ARRAY;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0 ; i < length; i++) {
            if (0 == i) {
                sb.append(ARRAY_START);
            } else {
                sb.append(ARRAY_ELEMENT_SEPARATOR);
            }
            sb.append(array[i]);
        }
        sb.append(ARRAY_END);
        return sb.toString();
    }
    public static String nullSafeToString(char[] array) {
        if (null == array) {
            return NULL_STRING;
        }
        int length = array.length;
        if (0 == length) {
            return EMPTY_ARRAY;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0 ; i < length; i++) {
            if (0 == i) {
                sb.append(ARRAY_START);
            } else {
                sb.append(ARRAY_ELEMENT_SEPARATOR);
            }
            sb.append(array[i]);
        }
        sb.append(ARRAY_END);
        return sb.toString();
    }
    public static String nullSafeToString(double[] array) {
        if (null == array) {
            return NULL_STRING;
        }
        int length = array.length;
        if (0 == length) {
            return EMPTY_ARRAY;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0 ; i < length; i++) {
            if (0 == i) {
                sb.append(ARRAY_START);
            } else {
                sb.append(ARRAY_ELEMENT_SEPARATOR);
            }
            sb.append(array[i]);
        }
        sb.append(ARRAY_END);
        return sb.toString();
    }
    public static String nullSafeToString(float[] array) {
        if (null == array) {
            return NULL_STRING;
        }
        int length = array.length;
        if (0 == length) {
            return EMPTY_ARRAY;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0 ; i < length; i++) {
            if (0 == i) {
                sb.append(ARRAY_START);
            } else {
                sb.append(ARRAY_ELEMENT_SEPARATOR);
            }
            sb.append(array[i]);
        }
        sb.append(ARRAY_END);
        return sb.toString();
    }
    public static String nullSafeToString(int[] array) {
        if (null == array) {
            return NULL_STRING;
        }
        int length = array.length;
        if (0 == length) {
            return EMPTY_ARRAY;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0 ; i < length; i++) {
            if (0 == i) {
                sb.append(ARRAY_START);
            } else {
                sb.append(ARRAY_ELEMENT_SEPARATOR);
            }
            sb.append(array[i]);
        }
        sb.append(ARRAY_END);
        return sb.toString();
    }
    public static String nullSafeToString(long[] array) {
        if (null == array) {
            return NULL_STRING;
        }
        int length = array.length;
        if (0 == length) {
            return EMPTY_ARRAY;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0 ; i < length; i++) {
            if (0 == i) {
                sb.append(ARRAY_START);
            } else {
                sb.append(ARRAY_ELEMENT_SEPARATOR);
            }
            sb.append(array[i]);
        }
        sb.append(ARRAY_END);
        return sb.toString();
    }
    public static String nullSafeToString(short[] array) {
        if (null == array) {
            return NULL_STRING;
        }
        int length = array.length;
        if (0 == length) {
            return EMPTY_ARRAY;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0 ; i < length; i++) {
            if (0 == i) {
                sb.append(ARRAY_START);
            } else {
                sb.append(ARRAY_ELEMENT_SEPARATOR);
            }
            sb.append(array[i]);
        }
        sb.append(ARRAY_END);
        return sb.toString();
    }

    public static Object unwrapOptional(Object obj) {
        if (obj instanceof Optional) {
            Optional<?> optional = (Optional<?>) obj;
            return optional.orElse(null);
        }
        return obj;
    }
}

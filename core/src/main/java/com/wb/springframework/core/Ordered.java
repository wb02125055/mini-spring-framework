package com.wb.springframework.core;

/**
 * @author WangBing
 * @date 2023/5/21 21:39
 */
public interface Ordered {
    int HIGHEST_PRECEDENCE = Integer.MIN_VALUE;

    int LOWEST_PRECEDENCE = Integer.MAX_VALUE;

    int getOrder();
}

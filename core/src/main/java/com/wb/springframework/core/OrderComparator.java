package com.wb.springframework.core;

import java.util.Comparator;

/**
 * @author WangBing
 * @date 2023/5/28 11:41
 */
public class OrderComparator implements Comparator<Object> {

    public static final OrderComparator INSTANCE = new OrderComparator();

    @Override
    public int compare(Object o1, Object o2) {
        return doCompare(o1, o2);
    }

    private int doCompare(Object o1, Object o2) {
        boolean p1 = o1 instanceof PriorityOrdered;
        boolean p2 = o2 instanceof PriorityOrdered;
        if (p1 && !p2) {
            return -1;
        } else if (p2 && !p1) {
            return 1;
        }
        int i1 = getOrder(o1);
        int i2 = getOrder(o2);
        return Integer.compare(i1, i2);
    }

    private int getOrder(Object obj) {
        if (obj != null) {
            Integer order = findOrder(obj);
            if (order != null) {
                return order;
            }
        }
        return Ordered.LOWEST_PRECEDENCE;
    }

    protected Integer findOrder(Object obj) {
        return obj instanceof Ordered ? ((Ordered) obj).getOrder() : null;
    }
}

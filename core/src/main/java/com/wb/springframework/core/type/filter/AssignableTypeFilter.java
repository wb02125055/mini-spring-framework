package com.wb.springframework.core.type.filter;

/**
 * @author WangBing
 * @date 2023/7/16 19:40
 */
public class AssignableTypeFilter extends AbstractTypeHierarchyTraversingFilter {

    private final Class<?> targetType;

    public AssignableTypeFilter(Class<?> targetType) {
        super(true, true);
        this.targetType = targetType;
    }

    public Class<?> getTargetType() {
        return targetType;
    }
}

package com.wb.springframework.core.type;

/**
 * @author WangBing
 * @date 2023/5/21 17:04
 */
public interface ClassMetadata {
    String getClassName();

    /**
     * 判断当前的bean定义指定的类是否为一个接口
     * @return 是否为接口
     */
    boolean isInterface();

    /**
     *
     * @return
     */
    String[] getMemberClassNames();
}

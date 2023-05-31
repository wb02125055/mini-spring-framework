package com.wb.springframework.core;

/**
 * @author WangBing
 * @date 2023/5/21 17:34
 */
public interface AliasRegistry {
    void registerAlias(String name, String alias);

    void removeAlias(String alias);

    boolean isAlias(String name);

    String[] getAliases(String name);
}

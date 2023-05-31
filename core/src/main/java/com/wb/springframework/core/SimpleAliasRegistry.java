package com.wb.springframework.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author WangBing
 * @date 2023/5/21 17:37
 */
public class SimpleAliasRegistry implements AliasRegistry {

    private final Map<String, String> aliasMap = new ConcurrentHashMap<>(16);

    @Override
    public void registerAlias(String name, String alias) {

    }

    @Override
    public void removeAlias(String alias) {

    }

    @Override
    public boolean isAlias(String name) {
        return false;
    }

    @Override
    public String[] getAliases(String name) {
        return new String[0];
    }

    public String canonicalName(String name) {
        String canonicalName = name;
        String resolvedName;
        do {
            resolvedName = this.aliasMap.get(canonicalName);
            if (resolvedName != null) {
                canonicalName = resolvedName;
            }
        } while (resolvedName != null);
        return canonicalName;
    }
}

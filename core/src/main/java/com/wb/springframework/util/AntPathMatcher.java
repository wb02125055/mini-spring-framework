package com.wb.springframework.util;

import java.util.Comparator;
import java.util.Map;

/**
 * @author WangBing
 * @date 2023/7/22 17:20
 */
public class AntPathMatcher implements PathMatcher {
    @Override
    public boolean isPattern(String path) {
        boolean uriVar = false;
        for (int i = 0; i < path.length(); i++) {
            char c = path.charAt(i);
            // 如果是以*或者?开头，直接返回匹配
            if (c == '*' || c == '?') {
                return true;
            }
            // 如果包含有占位符，则需要匹配占位符右边的括号.
            if (c == '{') {
                uriVar = true;
                continue;
            }
            // 匹配占位符右边括号 {}
            if (c == '}' && uriVar) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean match(String pattern, String path) {
        return false;
    }

    @Override
    public boolean matchStart(String pattern, String path) {
        return false;
    }

    @Override
    public String extractPathWithinPattern(String pattern, String path) {
        return null;
    }

    @Override
    public Map<String, String> extractUriTemplateVariables(String pattern, String path) {
        return null;
    }

    @Override
    public Comparator<String> getPatternComparator(String path) {
        return null;
    }

    @Override
    public String combine(String pattern1, String pattern2) {
        return null;
    }
}

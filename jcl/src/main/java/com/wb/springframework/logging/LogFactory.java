package com.wb.springframework.logging;

/**
 * @author WangBing
 * @date 2023/8/9 22:50
 */
public abstract class LogFactory {

    public static Log getLog(Class<?> clazz) {
        return getLog(clazz.getName());
    }

    public static Log getLog(String name) {
        return LogAdaptor.createLog(name);
    }
}

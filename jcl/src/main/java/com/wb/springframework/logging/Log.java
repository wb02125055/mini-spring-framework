package com.wb.springframework.logging;

/**
 * @author WangBing
 * @date 2023/8/9 22:22
 */
public interface Log {
    boolean isErrorEnabled();

    boolean isWarnEnabled();

    boolean isInfoEnabled();

    boolean isDebugEnabled();

    boolean isTraceEnabled();

    void error(Object message);

    void error(Object message, Throwable t);

    void warn(Object message);

    void warn(Object message, Throwable t);

    void info(Object message);

    void info(Object message, Throwable t);

    void debug(Object message);

    void debug(Object message, Throwable t);

    void trace(Object message);

    void trace(Object message, Throwable t);
}

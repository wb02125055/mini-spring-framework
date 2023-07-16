package com.wb.springframework.core.io;

/**
 * @author WangBing
 * @date 2023/6/20 21:24
 */
public abstract class AbstractResource implements Resource {
    @Override
    public boolean exists() {
        return false;
    }

    @Override
    public boolean isReadable() {
        return exists();
    }
}

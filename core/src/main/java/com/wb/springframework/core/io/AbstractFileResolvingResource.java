package com.wb.springframework.core.io;

/**
 * @author WangBing
 * @date 2023/7/22 17:49
 */
public abstract class AbstractFileResolvingResource extends AbstractResource {

    @Override
    public boolean exists() {
        return super.exists();
    }

    @Override
    public boolean isReadable() {
        return super.isReadable();
    }
}

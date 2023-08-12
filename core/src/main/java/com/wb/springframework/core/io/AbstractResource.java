package com.wb.springframework.core.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

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

    @Override
    public URL getURL() throws IOException {
        throw new FileNotFoundException(getDescription() + " cannot be resolved to URL");
    }

    @Override
    public File getFile() throws IOException {
        throw new FileNotFoundException(getDescription() + "  cannot be resolved to absolute file path");
    }
}

package com.wb.springframework.core.io;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * @author WangBing
 * @date 2023/6/20 21:21
 */
public interface Resource extends InputStreamSource {

    boolean exists();

    default boolean isReadable() {
        return exists();
    }

    URL getURL() throws IOException;

    File getFile() throws IOException;

    String getDescription();
}

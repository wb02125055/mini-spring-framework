package com.wb.springframework.core.io;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

/**
 * @author WangBing
 * @date 2023/8/9 23:11
 */
public interface WritableResource extends Resource {

    default boolean isWritable() {
        return true;
    }

    OutputStream getOutputStream() throws IOException;

    default WritableByteChannel writableChannel() throws IOException {
        return Channels.newChannel(getOutputStream());
    }
}

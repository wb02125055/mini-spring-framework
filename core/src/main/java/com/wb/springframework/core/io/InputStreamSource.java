package com.wb.springframework.core.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author WangBing
 * @date 2023/6/20 21:22
 */
public interface InputStreamSource {

    InputStream getInputStream() throws IOException;
}

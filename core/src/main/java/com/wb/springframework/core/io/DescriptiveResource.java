package com.wb.springframework.core.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author WangBing
 * @date 2023/6/20 21:24
 */
public class DescriptiveResource extends AbstractResource {

    private final String description;

    public DescriptiveResource(String description) {
        this.description = description;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        throw new FileNotFoundException(getDescription() + " cannot be opened because it does not point to a readable resource");
    }

    @Override
    public String getDescription() {
        return description;
    }
}

package com.wb.springframework.core.type.classreading;

import java.io.IOException;

/**
 * @author WangBing
 * @date 2023/6/16 23:21
 */
public interface MetadataReaderFactory {

    MetadataReader getMetadataReader(String className) throws IOException;

}

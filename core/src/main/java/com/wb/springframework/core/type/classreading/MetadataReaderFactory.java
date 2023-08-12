package com.wb.springframework.core.type.classreading;

import com.wb.springframework.core.io.Resource;

import java.io.IOException;

/**
 * @author WangBing
 * @date 2023/6/16 23:21
 */
public interface MetadataReaderFactory {

    MetadataReader getMetadataReader(String className) throws IOException;

    MetadataReader getMetadataReader(Resource resource) throws IOException;

}

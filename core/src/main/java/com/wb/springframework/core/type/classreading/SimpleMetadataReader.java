package com.wb.springframework.core.type.classreading;

import com.wb.springframework.asm.ClassReader;
import com.wb.springframework.core.io.Resource;
import com.wb.springframework.core.type.AnnotationMetadata;
import com.wb.springframework.core.type.ClassMetadata;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author WangBing
 * @date 2023/7/26 22:07
 */
public class SimpleMetadataReader implements MetadataReader {

    private final Resource resource;

    private final ClassMetadata classMetadata;

    private final AnnotationMetadata annotationMetadata;

    SimpleMetadataReader(Resource resource, ClassLoader classLoader) throws IOException {
        InputStream is = new BufferedInputStream(resource.getInputStream());
        ClassReader classReader;
        try {
            classReader = new ClassReader(is);
        } catch (IllegalArgumentException ex) {
            throw new IOException("ASM ClassReader failed to parse class file. maybe Java class version is not supported."
                    + resource,ex);
        } finally {
            is.close();
        }
        AnnotationMetadataReadingVisitor visitor = new AnnotationMetadataReadingVisitor(classLoader);
        classReader.accept(visitor, ClassReader.SKIP_DEBUG);

        this.annotationMetadata = visitor;
        this.classMetadata = visitor;
        this.resource = resource;
    }

    @Override
    public Resource getResource() {
        return null;
    }

    @Override
    public ClassMetadata getClassMetadata() {
        return null;
    }

    @Override
    public AnnotationMetadata getAnnotationMetadata() {
        return null;
    }
}

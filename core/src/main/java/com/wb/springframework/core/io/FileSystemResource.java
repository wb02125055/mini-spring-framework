package com.wb.springframework.core.io;

import com.wb.springframework.util.StringUtils;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

/**
 * @author WangBing
 * @date 2023/8/9 23:10
 */
public class FileSystemResource extends AbstractResource implements WritableResource {

    private final String path;

    private final File file;

    private final Path filePath;

    public FileSystemResource(String path) {
        this.path = StringUtils.cleanPath(path);
        this.file = new File(path);
        this.filePath = this.file.toPath();
    }

    public FileSystemResource(File file) {
        this.path = StringUtils.cleanPath(file.getPath());
        this.file = file;
        this.filePath = file.toPath();
    }

    @Override
    public boolean isWritable() {
        return this.file != null ? this.file.canWrite() && !this.file.isDirectory() :
                Files.isWritable(this.filePath) && !Files.isDirectory(this.filePath);
    }

    @Override
    public URL getURL() throws IOException {
        return this.file != null ? this.file.toURI().toURL() : this.filePath.toUri().toURL();
    }

    @Override
    public File getFile() throws IOException {
        return this.file != null ? this.file : this.filePath.toFile();
    }


    @Override
    public boolean isReadable() {
        return this.file != null ? this.file.canRead() && !this.file.isDirectory() :
                Files.isReadable(this.filePath) && !Files.isDirectory(this.filePath);
    }

    @Override
    public InputStream getInputStream() throws IOException {
        try {
            return Files.newInputStream(this.filePath);
        } catch (NoSuchFileException ex) {
            throw new FileNotFoundException(ex.getMessage());
        }
    }

    @Override
    public String getDescription() {
        return "file [" + (this.file != null ? this.file.getAbsolutePath() : this.filePath.toAbsolutePath()) + "]";
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return Files.newOutputStream(this.filePath);
    }

    public String getPath() {
        return path;
    }

    public Path getFilePath() {
        return filePath;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof FileSystemResource
                && this.path.equals(((FileSystemResource) obj).path));
    }

    @Override
    public int hashCode() {
        return this.path.hashCode();
    }
}

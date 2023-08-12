package com.wb.springframework.core.io.support;

import com.wb.springframework.core.io.*;
import com.wb.springframework.logging.Log;
import com.wb.springframework.logging.LogFactory;
import com.wb.springframework.util.AntPathMatcher;
import com.wb.springframework.util.PathMatcher;
import com.wb.springframework.util.ResourceUtils;
import com.wb.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author WangBing
 * @date 2023/7/16 19:56
 */
public class PathMatchingResourcePatternResolver implements ResourcePatternResolver {

    private static final Log log = LogFactory.getLog(PathMatchingResourcePatternResolver.class);

    private final ResourceLoader resourceLoader;

    private PathMatcher pathMatcher = new AntPathMatcher();

    public PathMatchingResourcePatternResolver() {
        this.resourceLoader = new DefaultResourceLoader();
    }

    public PathMatchingResourcePatternResolver(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }

    @Override
    public ClassLoader getClassLoader() {
        return getResourceLoader().getClassLoader();
    }

    @Override
    public Resource[] getResources(String locationPattern) throws IOException {
        if (locationPattern.startsWith(CLASSPATH_ALL_URL_PREFIX)) {
            if (getPathMatcher().isPattern(locationPattern.substring(CLASSPATH_ALL_URL_PREFIX.length()))) {
                return findPathMatchingResources(locationPattern);
            } else {
                return findAllClassPathResources(locationPattern.substring(CLASSPATH_ALL_URL_PREFIX.length()));
            }
        } else {
            int prefixEnd = locationPattern.startsWith("war:") ? locationPattern.indexOf("*/") + 1 :
                    locationPattern.indexOf(":") + 1;
            if (getPathMatcher().isPattern(locationPattern.substring(prefixEnd))) {
                return findPathMatchingResources(locationPattern);
            } else {
                return new Resource[]{
                        getResourceLoader().getResource(locationPattern)
                };
            }
        }
    }

    @Override
    public Resource getResource(String location) {
        return getResourceLoader().getResource(location);
    }

    public PathMatcher getPathMatcher() {
        return pathMatcher;
    }

    protected Resource[] findAllClassPathResources(String location) throws IOException {
        String path = location;
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        Set<Resource> result = doFindAllClassPathResources(path);
        return result.toArray(new Resource[0]);
    }

    protected Set<Resource> doFindAllClassPathResources(String path) throws IOException {
        Set<Resource> result = new LinkedHashSet<>(16);
        ClassLoader cl = getClassLoader();
        Enumeration<URL> resourceUrls = cl != null ? cl.getResources(path) : ClassLoader.getSystemResources(path);
        while (resourceUrls.hasMoreElements()) {
            URL url = resourceUrls.nextElement();
            result.add(convertClassLoaderURL(url));
        }
        if ("".equals(path)) {
            addAllClassLoaderJarRoots(cl, result);
        }
        return result;
    }

    protected void addAllClassLoaderJarRoots(ClassLoader classLoader, Set<Resource> result) {
        if (classLoader instanceof URLClassLoader) {
            try {
                for (URL url : ((URLClassLoader) classLoader).getURLs()) {
                    try {
                        UrlResource jarResource = (ResourceUtils.URL_PROTOCOL_JAR.equals(url.getProtocol()) ?
                                new UrlResource(url) :
                                new UrlResource(ResourceUtils.JAR_URL_PREFIX + url + ResourceUtils.JAR_URL_SEPARATOR));
                        if (jarResource.exists()) {
                            result.add(jarResource);
                        }
                    } catch (MalformedURLException e) {
                        System.err.println("错误: " + e.getMessage());
                        // no op
                    }
                }
            } catch (Exception e) {
                System.err.println("错误: " + e.getMessage());
                // no op
            }
        }

    }

    protected Resource convertClassLoaderURL(URL url) {
        return new UrlResource(url);
    }

    protected Resource[] findPathMatchingResources(String locationPattern) throws IOException {
        String rootDirPath = determineRootDir(locationPattern);
        String subPattern = locationPattern.substring(rootDirPath.length());
        Resource[] rootDirResources = getResources(rootDirPath);
        Set<Resource> result = new LinkedHashSet<>(16);
        for (Resource rootDirResource : rootDirResources) {
            rootDirResource = resolveRootDirResource(rootDirResource);
            URL rootDirUrl = rootDirResource.getURL();
            if (ResourceUtils.isJarUrl(rootDirUrl)) {
                result.addAll(doFindPathMatchingJarResources(rootDirResource, rootDirUrl, subPattern));
            } else {
                result.addAll(doFindPathMatchingFileResources(rootDirResource, subPattern));
            }
        }
        return result.toArray(new Resource[0]);
    }

    protected Set<Resource> doFindPathMatchingFileResources(Resource rootDirResource, String subPattern) throws IOException {
        File rootDir;
        rootDir = rootDirResource.getFile().getAbsoluteFile();
        return doFindMatchingFileSystemResources(rootDir, subPattern);
    }

    protected Set<Resource> doFindMatchingFileSystemResources(File rootDir, String subPattern) throws IOException {
        Set<File> matchingFiles = retrieveMatchingFiles(rootDir, subPattern);
        Set<Resource> result = new LinkedHashSet<>(matchingFiles.size());
        for (File file : matchingFiles) {
            result.add(new FileSystemResource(file));
        }
        return result;
    }

    protected Set<File> retrieveMatchingFiles(File rootDir, String pattern) throws IOException {
        if (!rootDir.exists()) {
            log.debug("文件路径不存在，返回空");
            return Collections.emptySet();
        }
        if (!rootDir.isDirectory()) {
            log.debug("文件路径不是文件夹，返回空");
            return Collections.emptySet();
        }
        if (!rootDir.canRead()) {
            log.debug("文件不可读，返回空");
            return Collections.emptySet();
        }
        String fullPattern = StringUtils.replace(rootDir.getAbsolutePath(), File.separator, "/");
        return null;
    }

    protected Set<Resource> doFindPathMatchingJarResources(Resource rootDirResource, URL rootDirURL, String subPattern) throws IOException {
        throw new UnsupportedOperationException("doFindPathMatchingJarResources还没有实现");
    }


    protected String determineRootDir(String location) {
        int prefixEnd = location.indexOf(":") + 1;
        int rootDirEnd = location.length();
        while (rootDirEnd > prefixEnd && getPathMatcher().isPattern(location.substring(prefixEnd, rootDirEnd))) {
            rootDirEnd = location.lastIndexOf('/', rootDirEnd - 2) + 1;
        }
        if (rootDirEnd == 0) {
            rootDirEnd = prefixEnd;
        }
        return location.substring(0, rootDirEnd);
    }

    protected Resource resolveRootDirResource(Resource original) throws IOException {
        return original;
    }


}

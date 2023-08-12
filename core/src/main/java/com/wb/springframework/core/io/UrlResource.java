package com.wb.springframework.core.io;

import com.wb.springframework.util.Assert;
import com.wb.springframework.util.ResourceUtils;
import com.wb.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;

/**
 * @author WangBing
 * @date 2023/7/22 17:50
 */
public class UrlResource extends AbstractFileResolvingResource {

    private final URI uri;
    private final URL url;

    private final URL cleanedUrl;

    public UrlResource(URL url) {
        Assert.notNull(url, "URL must not be null");
        this.url = url;
        this.uri = null;
        this.cleanedUrl = getCleanedUrl(this.url, url.toString());
    }

    public UrlResource(String path) throws MalformedURLException {
        this.uri = null;
        this.url = new URL(path);
        this.cleanedUrl = getCleanedUrl(this.url, path);
    }

    private URL getCleanedUrl(URL originalUrl, String originalPath) {
        String cleanedPath = StringUtils.cleanPath(originalPath);
        if (!cleanedPath.equals(originalPath)) {
            try {
                return new URL(cleanedPath);
            } catch (MalformedURLException e) {
                // no op
            }
        }
        return originalUrl;
    }

    public URL getCleanedUrl() {
        return cleanedUrl;
    }

    @Override
    public URL getURL() throws IOException {
        return this.url;
    }

    @Override
    public File getFile() throws IOException {
        URL url = getURL();
        return ResourceUtils.getFile(url, getDescription());
    }

    @Override
    public InputStream getInputStream() throws IOException {
        URLConnection con = this.url.openConnection();
        try {
            return con.getInputStream();
        } catch (IOException ex) {
            if (con instanceof HttpURLConnection) {
                ((HttpURLConnection) con).disconnect();
            }
            throw ex;
        }
    }

    @Override
    public String getDescription() {
        return "URL [" + this.url + "]";
    }
}

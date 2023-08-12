package com.wb.springframework.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author WangBing
 * @date 2023/7/22 18:09
 */
public abstract class ResourceUtils {

    public static final String URL_PROTOCOL_JAR = "jar";
    public static final String URL_PROTOCOL_WAR = "war";
    public static final String URL_PROTOCOL_ZIP = "zip";
    public static final String URL_PROTOCOL_FILE = "file";

    public static final String JAR_URL_PREFIX = "jar";

    public static final String JAR_URL_SEPARATOR = "!/";

    public static boolean isJarUrl(URL url) {
        String protocol = url.getProtocol();
        return URL_PROTOCOL_JAR.equals(protocol) ||
                URL_PROTOCOL_WAR.equals(protocol) ||
                URL_PROTOCOL_ZIP.equals(protocol);
    }

    public static File getFile(URL resourceUrl, String description) throws FileNotFoundException {
        if (!URL_PROTOCOL_FILE.equals(resourceUrl.getProtocol())) {
            throw new FileNotFoundException(description + " 指定的url无法被解析为文件 ");
        }
        try {
            return new File(toURI(resourceUrl).getSchemeSpecificPart());
        } catch (Exception e) {
            return new File(resourceUrl.getFile());
        }
    }

    public static URI toURI(URL url) throws Exception {
        return toURI(url.toString());
    }

    public static URI toURI(String location) throws Exception {
        return new URI(StringUtils.replace(location, " ", "%20"));
    }
}

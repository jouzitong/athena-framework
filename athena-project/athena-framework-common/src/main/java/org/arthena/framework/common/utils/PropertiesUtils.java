package org.arthena.framework.common.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

/**
 * @author zhouzhitong
 * @since 2025/6/9
 **/
public class PropertiesUtils {



    public static Properties loadAllProperties(String fileName) throws IOException {
        Properties mergedProperties = new Properties();

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources = classLoader.getResources(fileName);

        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            try (InputStream stream = url.openStream()) {
                InputStreamReader reader = new InputStreamReader(stream);
                Properties props = new Properties();
                props.load(reader);
                mergedProperties.putAll(props); // 后加载的可以覆盖前面的
            }
        }
        return mergedProperties;
    }

}

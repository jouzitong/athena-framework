package org.arthena.framework.common.utils;

import org.arthena.framework.common.constant.CodeConstant;
import org.arthena.framework.common.context.SystemContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ErrorCodeUtils 类提供了一组工具方法来处理错误码相关的操作，包括获取错误信息、加载错误码文件等。
 * 该类支持多语言环境下的错误码解析，并能够从自定义和默认的错误码文件中读取错误信息。
 * 错误码文件格式为 .properties 文件。
 */
public class ErrorCodeUtils {

    public static final String CUSTOM = "CUSTOM_";

    public static final String ERROR_CODE_PREFIX = "ErrorCode-";

    // TODO
    public static final String CUSTOM_ERROR_CODE_FILE = "config/ErrorCode-";

    public static final String FILE_TYPE = ".properties";

    public static final String CODE_MSG_PARAM_PLACEHOLDER = "{}";

    private static final Map<String, Properties> errorCodeMap = new ConcurrentHashMap<>();
    private static final Logger log = LoggerFactory.getLogger(ErrorCodeUtils.class);

    public static String getMsg(Integer code, Object... args) {
        if (code == null) {
            return CodeConstant.UN_KNOW_ERROR_MSG;
        }
        String msg = getMsg(code);
        if (msg == null) {
            msg = CodeConstant.UN_KNOW_ERROR_MSG;
        }
        if (args == null || args.length == 0) {
            return msg;
        }
        return replacePlaceholders(msg, args);
    }

    private static String getMsg(Integer code) {
        String locale = resolveLocale();
        try {
            String msg = getCustomMsg(code, locale);
            if (msg != null) {
                return msg;
            }
        } catch (Exception e) {
            log.error("加载 CUSTOM 错误码文件({})失败. ", locale, e);
        }
        return getDefaultMsg(code, locale);
    }

    private static String resolveLocale() {
        String localeStr = SystemContext.getLocale();
        if (localeStr == null || localeStr.isBlank()) {
            return "zh";
        }
        int underscoreIndex = localeStr.indexOf("_");
        if (underscoreIndex <= 0) {
            return localeStr;
        }
        return localeStr.substring(0, underscoreIndex);
    }

    private static String replacePlaceholders(String msg, Object[] args) {
        String result = msg;
        for (Object arg : args) {
            int placeholderIndex = result.indexOf(CODE_MSG_PARAM_PLACEHOLDER);
            if (placeholderIndex < 0) {
                break;
            }
            String replacement = arg == null ? "" : String.valueOf(arg);
            result = result.substring(0, placeholderIndex) + replacement
                + result.substring(placeholderIndex + CODE_MSG_PARAM_PLACEHOLDER.length());
        }
        return result;
    }

    /**
     * 获取默认错误码
     *
     * @param code   错误码
     * @param locale 语言
     * @return 错误码
     */
    private static String getDefaultMsg(Integer code, String locale) {
        String name = ERROR_CODE_PREFIX + locale + FILE_TYPE;
        Properties properties = loadErrorCode(locale, name);
        if (properties == null) {
            return CodeConstant.UN_KNOW_ERROR_MSG;
        }
        String msg = properties.getProperty(code.toString());
        if (msg != null) {
            return msg;
        }
        return CodeConstant.UN_KNOW_ERROR_MSG;
    }

    /**
     * 获取自定义错误码
     *
     * @param code   错误码
     * @param locale 语言
     * @return 自定义错误码
     */
    private static String getCustomMsg(Integer code, String locale) {
        String name = CUSTOM_ERROR_CODE_FILE + locale + FILE_TYPE;
        Properties properties = loadErrorCode(CUSTOM + locale, name);
        if (properties == null) {
            return null;
        }
        return properties.getProperty(code.toString());
    }

    /**
     * 加载错误码文件
     *
     * @param key      key
     * @param fileName 文件名
     * @return 文件内容
     */
    private static Properties loadErrorCode(String key, String fileName) {
        if (errorCodeMap.containsKey(key)) {
            return errorCodeMap.get(key);
        }
        synchronized (key.intern()) {
            if (errorCodeMap.containsKey(key)) {
                return errorCodeMap.get(key);
            }
            try {
                Properties properties = PropertiesUtils.loadAllProperties(fileName);
                errorCodeMap.put(key, properties);
                return properties;
            } catch (Exception e) {
                log.error("加载错误码文件({})失败. ", fileName, e);
                return null;
            }
        }
    }

}

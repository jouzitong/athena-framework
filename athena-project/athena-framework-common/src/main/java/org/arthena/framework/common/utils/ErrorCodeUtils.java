package org.arthena.framework.common.utils;

import org.arthena.framework.common.constant.CodeConstant;
import org.arthena.framework.common.context.SystemContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhouzhitong
 * @since 2025/6/9
 **/
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
        String msg = getMsg(code);
        if (args == null) {
            return msg;
        }
        int msgIndex = msg.indexOf(CODE_MSG_PARAM_PLACEHOLDER);
        int argsIndex = 0;
        while (msgIndex != -1) {
            String rMsg = args.length > argsIndex ? " " + args[argsIndex++].toString() + " " : "";
            msg = msg.replace(CODE_MSG_PARAM_PLACEHOLDER, rMsg);
            msgIndex = msg.indexOf(CODE_MSG_PARAM_PLACEHOLDER, msgIndex + 1);
        }
        return msg;
    }

    private static String getMsg(Integer code) {
        String localeStr = SystemContext.getLocale();
        String locale = localeStr.substring(0, localeStr.indexOf("_"));
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
            return null;
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
        Properties properties = loadErrorCode(CUSTOM, name);
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

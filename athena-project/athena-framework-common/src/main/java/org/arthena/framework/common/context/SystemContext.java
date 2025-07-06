package org.arthena.framework.common.context;

import org.arthena.framework.common.constant.LocaleConstant;
import org.arthena.framework.common.constant.SystemContextKeyConstant;

import java.util.HashMap;
import java.util.Map;

/**
 * 系统参数, 获取系统的语言环境, 当前操作用户等等信息...
 *
 * @author zhouzhitong
 * @version 1.0
 * @since 2022/6/18
 */
public class SystemContext {

    /**
     * 系统默认语言支持: zh_CN
     */
    public static final String DEFAULT_LOCALE = LocaleConstant.DEFAULT_LOCALE;

    /**
     * 系统默认操作用户: 0L
     */
    public static final Long DEFAULT_OPERATOR = 0L;

    private static final ThreadLocal<Map<String, Object>> CONTEXT_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 系统是否启动
     */
    private static volatile Boolean isRunning = false;

    public static String getDefaultLocale() {
        return DEFAULT_LOCALE;
    }

    public static void setLocale(String key, Object val) {
        Map<String, Object> map = CONTEXT_THREAD_LOCAL.get();
        if (map == null) {
            map = new HashMap<>();
            CONTEXT_THREAD_LOCAL.set(map);
        }
        map.put(key, val);
    }

    public static String getLocale() {
        Object locale = get(SystemContextKeyConstant.LOCALE);
        if (locale != null) {
            return locale.toString();
        }
        return DEFAULT_LOCALE;
    }

    public static Object get(String key) {
        Map<String, Object> map = CONTEXT_THREAD_LOCAL.get();
        if (map != null) {
            return map.get(key);
        }
        return null;
    }

    public static void removeLocale() {
        CONTEXT_THREAD_LOCAL.remove();
    }

    public static void finish() {
        isRunning = true;
    }

    public static Boolean isRunning() {
        return isRunning;
    }

}

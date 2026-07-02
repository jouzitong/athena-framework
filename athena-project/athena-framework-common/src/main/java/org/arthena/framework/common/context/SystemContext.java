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
    public static final Long DEFAULT_OPERATOR = 999999999L;

    private static final ThreadLocal<Map<String, Object>> CONTEXT_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 系统是否启动
     */
    private static volatile Boolean isRunning = false;

    public static String getDefaultLocale() {
        return DEFAULT_LOCALE;
    }

    public static void setLocale(String locale) {
        put(SystemContextKeyConstant.LOCALE, locale);
    }

    public static void clearLocale() {
        remove(SystemContextKeyConstant.LOCALE);
    }

    public static void setUserContext(Object userContext) {
        put(SystemContextKeyConstant.USER_CONTEXT, userContext);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getUserContext() {
        return (T) getValue(SystemContextKeyConstant.USER_CONTEXT);
    }

    public static void clearUserContext() {
        remove(SystemContextKeyConstant.USER_CONTEXT);
    }

    public static void setTenantId(String tenantId) {
        put(SystemContextKeyConstant.TENANT_ID, tenantId);
    }

    public static String getTenantId() {
        Object tenantId = getValue(SystemContextKeyConstant.TENANT_ID);
        return tenantId == null ? null : tenantId.toString();
    }

    public static void clearTenantId() {
        remove(SystemContextKeyConstant.TENANT_ID);
    }

    private static void put(String key, Object val) {
        Map<String, Object> map = CONTEXT_THREAD_LOCAL.get();
        if (map == null) {
            map = new HashMap<>();
            CONTEXT_THREAD_LOCAL.set(map);
        }
        map.put(key, val);
    }

    public static String getLocale() {
        Object locale = getValue(SystemContextKeyConstant.LOCALE);
        if (locale != null) {
            return locale.toString();
        }
        return DEFAULT_LOCALE;
    }

    private static Object getValue(String key) {
        Map<String, Object> map = CONTEXT_THREAD_LOCAL.get();
        if (map != null) {
            return map.get(key);
        }
        return null;
    }

    private static void remove(String key) {
        Map<String, Object> map = CONTEXT_THREAD_LOCAL.get();
        if (map == null) {
            return;
        }
        map.remove(key);
        if (map.isEmpty()) {
            CONTEXT_THREAD_LOCAL.remove();
        }
    }

    public static Map<String, Object> snapshot() {
        Map<String, Object> map = CONTEXT_THREAD_LOCAL.get();
        if (map == null || map.isEmpty()) {
            return null;
        }
        return new HashMap<>(map);
    }

    public static void restore(Map<String, Object> context) {
        if (context == null || context.isEmpty()) {
            CONTEXT_THREAD_LOCAL.remove();
            return;
        }
        CONTEXT_THREAD_LOCAL.set(new HashMap<>(context));
    }

    public static void clear() {
        CONTEXT_THREAD_LOCAL.remove();
    }

    public static void removeLocale() {
        clearLocale();
    }

    public static void finish() {
        isRunning = true;
    }

    public static Boolean isRunning() {
        return isRunning;
    }


    public static long currentTimeMillis(){
        return System.currentTimeMillis();
    }

}

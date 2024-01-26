package org.arthena.framework.common.context;

import org.apache.commons.lang3.StringUtils;

/**
 * 系统参数, 获取系统的语言环境, 当前操作用户等等信息...
 *
 * @author zhouzhitong
 * @version 1.0
 * @since 2022/6/18
 */
public class SystemContext {

    /**
     * 中文
     */
    public static final String LOCALE_ZH_CN = "zh_CN";

    /**
     * 系统默认语言支持: zh_CN
     */
    public static final String DEFAULT_LOCALE = LOCALE_ZH_CN;

    /**
     * 系统默认操作用户: 0L
     */
    public static final Long DEFAULT_OPERATOR = 0L;

    private static final ThreadLocal<UserContext> USER_CONTEXT_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 系统是否启动
     */
    private static volatile Boolean isRunning = false;

    public static void set(UserContext uc) {
        USER_CONTEXT_THREAD_LOCAL.set(uc);
    }

    public static void remove() {
        USER_CONTEXT_THREAD_LOCAL.remove();
    }

    /**
     * 获取当前操作用户
     *
     * @return
     */
    public static Long getUserId() {
        UserContext userContext = USER_CONTEXT_THREAD_LOCAL.get();
        if (userContext != null) {
            return userContext.getUserId();
        }
        return DEFAULT_OPERATOR;
    }

    /**
     * 获取当前操作语言
     *
     * @return 当前操作语言
     */
    public static String getLocale() {
        UserContext userContext = USER_CONTEXT_THREAD_LOCAL.get();
        if (userContext != null) {
            return userContext.getLang();
        }
        return getDefaultLocale();
    }

    public static boolean isDefaultLocale() {
        return StringUtils.equals(getLocale(), getLocale());
    }

    public static String getDefaultLocale() {
        return DEFAULT_LOCALE;
    }

    public static void finish() {
        isRunning = true;
    }

    public static Boolean isRunning() {
        return isRunning;
    }

}

package org.arthena.framework.common.utils;

/**
 * TODO 设计这个类主要是为了解决 日志国际化的问题, 但是这个类还没有实现, 也没设计好
 *
 * @author zhouzhitong
 * @since 2024-03-14
 **/
public class StringManager {

    public static String convertMessage(String msg) {
        return msg;
    }

    public static String convertMessage(String msg, Object... args) {
        return String.format(msg, args);
    }

}

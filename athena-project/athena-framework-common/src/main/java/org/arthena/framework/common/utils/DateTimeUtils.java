package org.arthena.framework.common.utils;

import org.arthena.framework.common.context.SystemContext;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author zhouzhitong
 * @since 2024/4/24
 **/
public class DateTimeUtils {

    public static final TimeUnit BASE_TIME_UNIT = TimeUnit.SECONDS;

    public static final ZoneOffset BASE_ZONE_OFFSET = ZoneOffset.of("+8");

    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static final long TIME_OUT_MS = 300;

    /**
     * 解析日期格式 yyyy-MM
     *
     * @param dateString 格式: yyyy-MM
     * @return 下个月的时间戳
     */
    public static long parseAndNextMonth(String dateString) {
        LocalDate date = LocalDate.parse(dateString + "-01");
        LocalDateTime time = date.atStartOfDay();
        // 返回下一个月的第一天
        time = time.plusMonths(1);
        return ofMilliSeconds(time);
    }

    /**
     * 获取当前时间戳
     *
     * @param timestamp 时间戳, 单位: 毫秒
     * @return LocalDateTime
     */
    public static LocalDateTime ofTime(long timestamp) {
        return LocalDateTime.ofEpochSecond(timestamp / 1000, 0, BASE_ZONE_OFFSET);
    }

    /**
     * 获取当前时间戳字符串
     *
     * @param timestamp 时间戳, 单位: 毫秒
     * @return LocalDateTime
     */
    public static String ofTimeStr(long timestamp) {
        LocalDateTime dt = ofTime(timestamp);
        return dt.format(TIME_FORMATTER);
    }

    /**
     * 将 LocalDateTime 转换为时间戳
     *
     * @param time LocalDateTime
     * @return 时间戳, 单位: 毫秒
     */
    public static long ofMilliSeconds(LocalDateTime time) {
        return time.toEpochSecond(BASE_ZONE_OFFSET) * 1000;
    }

    /**
     * 获取当前月的最开始时间戳
     *
     * @return 前月的最开始时间戳
     */
    public static long getFirstTimeOfMonth() {
        LocalDateTime now = LocalDateTime.now();
        return ofMilliSeconds(now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0));
    }

    /**
     * 将耗时时长转换成日期字符串
     *
     * @param ts 耗时. 毫秒
     * @return 日期字符串.
     * 毫秒秒: xx MS
     * 秒: xx S
     * 分: xx M
     * 时: xx H
     * 天: xx D
     */
    public static String takeToStr(long ts) {
        if (ts == 0) {
            return "";
        }
        if (ts < 1000L) {
            return ts + "MS ";
        }
        if (ts < 60000L) {
            return ts / 1000 + "S " + takeToStr(ts % 1000);
        }
        if (ts < 3600000L) {

            return ts / 60000 + "M " + takeToStr(ts % 60000);
        }
        if (ts < 86400000L) {
            return ts / 3600000 + "H " + takeToStr(ts % 3600000L);
        }
        return ts / 86400000 + "D " + takeToStr(ts % 86400000L);
    }

    /**
     * 获取上个月的最开始时间戳
     *
     * @return 前月的最开始时间戳
     */
    public static long getLastTimeOfMonth() {
        LocalDateTime now = LocalDateTime.now();
        return ofMilliSeconds(now.minusMonths(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0));
    }

    /**
     * 获取上个月的最开始时间戳
     *
     * @return 前月的最开始时间戳
     */
    public static long getLastTimeOfMonth(int month) {
        LocalDateTime now = LocalDateTime.now();
        return ofMilliSeconds(now.minusMonths(1).withDayOfMonth(month).withHour(0).withMinute(0).withSecond(0).withNano(0));
    }

    public static long ofMilliSeconds(String date) {
        // yyyy-MM => yyyy-MM-01
        if (date.length() == 7) {
            date += "-01";
        }
        // 判断日期格式是否是 yyyy-MM-dd
        LocalDateTime time;
        if (date.length() == 10) {
            date += "T00:00:00";
        }
        time = LocalDateTime.parse(date);
        return time.toEpochSecond(BASE_ZONE_OFFSET) * 1000;
    }

    public static List<String> filter(File[] files, long ts) {
        LocalDateTime localDateTime = ofTime(ts);
        // 截取前7个字符
        String tsStr = localDateTime.toString().substring(0, 7);
        List<String> res = new ArrayList<>();
        for (File file : files) {
            String name = file.getName();

        }
        return res;
    }

    /**
     * 将时间戳转换为指定时间单位的时间戳
     *
     * @param timestamp 时间戳, 单位: 毫秒
     * @return 新的时间戳【指定时间单位】
     */
    public static long ofUnit(long timestamp) {
        return BASE_TIME_UNIT.convert(timestamp, TimeUnit.MILLISECONDS);
    }

    public static boolean isTimeOut(long ts) {
        long cur = SystemContext.currentTimeMillis();
        return cur - ts > TIME_OUT_MS;
    }

    public static void main(String[] args) {
//        System.out.println(takeToStr(1200L * 60 * 60 * 4 + 2000 + 23));
//        System.out.println(ofSecond("2023-03-01"));
        System.out.println(ofMilliSeconds("2024-12-10T05:00:00"));
        System.out.println(parseAndNextMonth("2023-03"));
        System.out.println(ofTime(1757354340000L));
        LocalDateTime now = LocalDateTime.now();
        System.out.println("当前月份最开始的时候: " + now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0));
        System.out.println("当上月份最开始的时候: " + now.minusMonths(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0));
        System.out.println(now);
        long l = ofMilliSeconds(now);
        long l1 = System.currentTimeMillis();
        System.out.println(l);
        System.out.println(l1);
        LocalDateTime time = ofTime(l);
        System.out.println(time);
    }


}

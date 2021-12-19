package com.desen.common.utils;

import org.springframework.util.Assert;

import java.security.acl.LastOwnerException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;

public class DateTimeUtil {

    // 时间元素
    private static final String YEAR = "year";
    private static final String MONTH = "month";
    private static final String WEEK = "week";
    private static final String DAY = "day";
    private static final String HOUR = "hour";
    private static final String MINUTE = "minute";
    private static final String SECOND = "second";
    // 根据指定格式显示日期和时间
    /**
     * yyyy-MM-dd （默认日期格式）
     */
    private static final DateTimeFormatter yyyyMMdd_EN = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    /**
     * yyyy-MM-dd HH
     */
    private static final DateTimeFormatter yyyyMMddHH_EN = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
    /**
     * yyyy-MM-dd HH:mm
     */
    private static final DateTimeFormatter yyyyMMddHHmm_EN = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    /**
     * yyyy-MM-dd HH:mm:ss （默认日期时间格式）
     */
    private static final DateTimeFormatter yyyyMMddHHmmss_EN = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    /**
     * HH:mm:ss （默认时间格式）
     */
    private static final DateTimeFormatter HHmmss_EN = DateTimeFormatter.ofPattern("HH:mm:ss");
    /**
     * yyyy年MM月dd日
     */
    private static final DateTimeFormatter yyyyMMdd_CN = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
    /**
     * yyyy年MM月dd日HH时
     */
    private static final DateTimeFormatter yyyyMMddHH_CN = DateTimeFormatter.ofPattern("yyyy年MM月dd日HH时");
    /**
     * yyyy年MM月dd日HH时mm分
     */
    private static final DateTimeFormatter yyyyMMddHHmm_CN = DateTimeFormatter.ofPattern("yyyy年MM月dd日HH时mm分");
    /**
     * yyyy年MM月dd日HH时mm分ss秒
     */
    private static final DateTimeFormatter yyyyMMddHHmmss_CN = DateTimeFormatter.ofPattern("yyyy年MM月dd日HH时mm分ss秒");
    /**
     * HH时mm分ss秒
     */
    private static final DateTimeFormatter HHmmss_CN = DateTimeFormatter.ofPattern("HH时mm分ss秒");

    // 本地时间显示格式：区分中文和外文显示
    // 本地时间显示格式：区分中文和外文显示
    private static final DateTimeFormatter shotDate = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
    private static final DateTimeFormatter fullDate = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL);
    private static final DateTimeFormatter longDate = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG);
    private static final DateTimeFormatter mediumDate = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);


    /**
     * 计算两个日期时间之间相差多少年
     */
    public static long betweenYears(LocalDateTime start, LocalDateTime end) {
        return between(start, end, ChronoUnit.YEARS);
    }

    /**
     * 计算两个日期时间之间相差多少年
     */
    public static long betweenYears(String start, String end) {
        return between(start, end, ChronoUnit.YEARS);
    }

    /**
     * 计算两个日期时间之间相差多少月
     */
    public static long betweenMonths(LocalDateTime start, LocalDateTime end) {
        return between(start, end, ChronoUnit.MONTHS);
    }

    /**
     * 计算两个日期时间之间相差多少月
     */
    public static long betweenMonths(String start, String end) {
        return between(start, end, ChronoUnit.MONTHS);
    }

    /**
     * 计算两个日期时间之间相差多少天
     */
    public static long betweenDays(LocalDateTime start, LocalDateTime end) {
        return between(start, end, ChronoUnit.DAYS);
    }

    /**
     * 计算两个日期时间之间相差多少天
     */
    public static long betweenDays(String start, String end) {
        return between(start, end, ChronoUnit.DAYS);
    }

    /**
     * 计算两个日期时间之间相差多少小时
     */
    public static long betweenHours(LocalDateTime start, LocalDateTime end) {
        return between(start, end, ChronoUnit.HOURS);
    }

    /**
     * 计算两个日期时间之间相差多少小时
     */
    public static long betweenHours(String start, String end) {
        return between(start, end, ChronoUnit.HOURS);
    }

    /**
     * 计算两个日期时间之间相差多少分钟
     */
    public static long betweenMinutes(LocalDateTime start, LocalDateTime end) {
        return between(start, end, ChronoUnit.MINUTES);
    }

    /**
     * 计算两个日期时间之间相差多少分钟
     */
    public static long betweenMinutes(String start, String end) {
        return between(start, end, ChronoUnit.MINUTES);
    }

    /**
     * 计算两个日期时间之间相差多少秒
     */
    public static long betweenSeconds(LocalDateTime start, LocalDateTime end) {
        return between(start, end, ChronoUnit.SECONDS);
    }

    /**
     * 计算两个日期时间之间相差多少秒
     */
    public static long betweenSeconds(String start, String end) {
        return between(start, end, ChronoUnit.SECONDS);
    }

    /**
     * 计算两个日期时间之间相差多少毫秒
     */
    public static long betweenMillis(LocalDateTime start, LocalDateTime end) {
        return between(start, end, ChronoUnit.MILLIS);
    }

    /**
     * 计算两个日期时间之间相差多少毫秒
     */
    public static long betweenMillis(String start, String end) {
        return between(start, end, ChronoUnit.MILLIS);
    }

    /**
     * 计算两个日期时间之间相差多少个周期（年、月、日、时、分、秒）
     * 注意：左包含[start,end)
     * @param start LocalDateTime
     * @param end LocalDateTime
     * @param unit ChronoUnit
     * @return long 相差多少周期单位
     */
    public static long between(LocalDateTime start, LocalDateTime end, ChronoUnit unit) {
        Assert.notNull(start,"开始时间不能为空");
        Assert.notNull(end,"结束时间不能为空");
        Assert.notNull(unit,"返回时间单位不能为空");
        return unit.between(start, end);
    }
    /**
     * 计算两个日期时间之间相差多少个周期（年、月、日、时、分、秒）
     * 注意：左包含[start,end)
     * @param start String
     * @param end String
     * @param unit ChronoUnit
     * @return long 相差多少周期单位
     */
    public static long between(String start, String end, ChronoUnit unit) {
        Assert.notNull(start,"开始时间不能为空");
        Assert.notNull(end,"结束时间不能为空");
        Assert.notNull(unit,"返回时间的单位不能为空");
        LocalDateTime startDateTime = LocalDateTime.parse(start, yyyyMMddHHmmss_EN);
        LocalDateTime endDateTime = LocalDateTime.parse(end, yyyyMMddHHmmss_EN);
        return unit.between(startDateTime, endDateTime);
    }


    public static void main(String[] args) {
        LocalDateTime dateTime1 = LocalDateTime.of(2021, 12, 12, 12, 13, 10);
        LocalDateTime dateTime2 = LocalDateTime.of(2021, 12, 20, 12, 13, 11);
        System.out.println(betweenDays(dateTime2,dateTime1));
        System.out.println(between("2021-12-11 12:13:10","2021-12-12 12:14:10",ChronoUnit.DAYS));

    }
}

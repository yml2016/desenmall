package com.desen.common.utils;

import org.springframework.util.Assert;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
     * 计算两个日期时间之间相差多少星期
     */
    public static long betweenWeeks(String start, String end) {
        return between(start, end, ChronoUnit.WEEKS);
    }

    /**
     * 计算两个日期时间之间相差多少星期
     */
    public static long betweenWeeks(LocalDateTime start, LocalDateTime end) {
        return between(start, end, ChronoUnit.WEEKS);
    }

    /**
     * 计算两个日期时间之间相差多少个周期（年、月、日、时、分、秒，周）
     * 注意：左包含[start,end)
     *
     * @param start LocalDateTime
     * @param end   LocalDateTime
     * @param unit  ChronoUnit
     * @return long 相差多少周期单位
     */
    public static long between(LocalDateTime start, LocalDateTime end, ChronoUnit unit) {
        Assert.notNull(start, "开始时间不能为空");
        Assert.notNull(end, "结束时间不能为空");
        Assert.notNull(unit, "返回时间单位不能为空");
        return unit.between(start, end);
    }

    /**
     * 计算两个日期时间之间相差多少个周期（年、月、日、时、分、秒）
     * 注意：左包含[start,end)
     *
     * @param start String
     * @param end   String
     * @param unit  ChronoUnit
     * @return long 相差多少周期单位
     */
    public static long between(String start, String end, ChronoUnit unit) {
        LocalDateTime startDateTime = LocalDateTime.parse(start, yyyyMMddHHmmss_EN);
        LocalDateTime endDateTime = LocalDateTime.parse(end, yyyyMMddHHmmss_EN);
        return between(startDateTime, endDateTime, unit);
    }

    /**
     * 计算两个日期时间之间相差多少年，月，日
     * @param startInclusive
     * @param endExclusive
     * @return TimeInterval
     */
    private static TimeInterval period(LocalDate startInclusive, LocalDate endExclusive) {
        Period period = Period.between(startInclusive, endExclusive);
        TimeInterval timeInterval = new TimeInterval();
        timeInterval.setYears(period.getYears());
        timeInterval.setMonths(period.getMonths());
        timeInterval.setDays(period.getDays());
        return timeInterval;
    }


    /**
     * 计算两个日期时间之间相差多少时，分，秒，毫秒
     * @param startInclusive
     * @param endExclusive
     * @return TimeInterval
     */
    private static TimeInterval duration(LocalDateTime startInclusive, LocalDateTime endExclusive) {
        Duration duration = Duration.between(startInclusive, endExclusive);
        TimeInterval timeInterval = new TimeInterval();
        //timeInterval.setDays(duration.toDays());//相差的总天数
        timeInterval.setHours(duration.toHours() % 24);
        timeInterval.setMinutes(duration.toMinutes() % 60);
        timeInterval.setSeconds(duration.getSeconds() % 60);
        timeInterval.setMillis(duration.toMillis() % 1000);
        return timeInterval;
    }


    /**
     * 计算两个日期时间之间相差多少年，月，日，时，分，秒，毫秒
     * @param startInclusive
     * @param endExclusive
     * @return TimeInterval
     */
    public static TimeInterval between(LocalDateTime startInclusive, LocalDateTime endExclusive) {
        Assert.notNull(startInclusive, "开始时间不能为空");
        Assert.notNull(endExclusive, "结束时间不能为空");
        TimeInterval dateInterval = period(startInclusive.toLocalDate(), endExclusive.toLocalDate());
        TimeInterval timeInterval = duration(startInclusive, endExclusive);
        TimeInterval interval = new TimeInterval();
        interval.setYears(dateInterval.getYears())
                .setMonths(dateInterval.getMonths())
                .setDays(dateInterval.getDays())
                .setHours(timeInterval.getHours())
                .setMinutes(timeInterval.getMinutes())
                .setSeconds(timeInterval.getSeconds())
                .setMillis(timeInterval.getMillis());
        return interval;
    }
    /**
     * 计算两个日期时间之间相差多少年，月，日，时，分，秒，毫秒
     * @param startInclusive
     * @param endExclusive
     * @return TimeInterval
     */
    public static TimeInterval between(String startInclusive, String endExclusive) {
        LocalDateTime start = LocalDateTime.parse(startInclusive, yyyyMMddHHmmss_EN);
        LocalDateTime end = LocalDateTime.parse(endExclusive, yyyyMMddHHmmss_EN);
        return between(start, end);
    }

//==================================================================================================
    /**
     * 指定日期时间是否在指定范围内
     * 保质期内
     * @param checkDateTime   被检查的时间
     * @param startDateTime 起始时间
     * @param endDateTime   结束时间
     * @return 是否在范围内
     */
    public static boolean isIn(String checkDateTime, String startDateTime, String endDateTime) {
        Assert.notNull(checkDateTime, "指定时间不能为空");
        Assert.notNull(startDateTime, "开始时间不能为空");
        Assert.notNull(endDateTime, "结束时间不能为空");
        LocalDateTime date = LocalDateTime.parse(checkDateTime, yyyyMMddHHmmss_EN);
        LocalDateTime start = LocalDateTime.parse(startDateTime, yyyyMMddHHmmss_EN);
        LocalDateTime end = LocalDateTime.parse(endDateTime, yyyyMMddHHmmss_EN);
        return date.isAfter(start) && date.isBefore(end);
    }

    /**
     * 指定日期时间是否在某日期时间之后
     * @param checkDateTime
     * @param startDateTime
     * @return
     */
    public static boolean isAfter(String checkDateTime, String startDateTime) {
        Assert.notNull(checkDateTime, "指定时间不能为空");
        Assert.notNull(startDateTime, "开始时间不能为空");
        LocalDateTime date = LocalDateTime.parse(checkDateTime, yyyyMMddHHmmss_EN);
        LocalDateTime start = LocalDateTime.parse(startDateTime, yyyyMMddHHmmss_EN);
        return date.isAfter(start);
    }

    /**
     * 指定日期时间是否在某日期时间之前
     * @param checkDateTime
     * @param endDateTime
     * @return
     */
    public static boolean isBefore(String checkDateTime, String endDateTime) {
        Assert.notNull(checkDateTime, "指定时间不能为空");
        Assert.notNull(endDateTime, "开始时间不能为空");
        LocalDateTime date = LocalDateTime.parse(checkDateTime, yyyyMMddHHmmss_EN);
        LocalDateTime end = LocalDateTime.parse(endDateTime, yyyyMMddHHmmss_EN);
        return date.isBefore(end);
    }

    /**
     * 指定日期时间是否和某日期时间相等
     * @param checkDateTime
     * @param dateTime
     * @return
     */
    public static boolean isSame(String checkDateTime, String dateTime) {
        Assert.notNull(checkDateTime, "指定时间不能为空");
        Assert.notNull(dateTime, "对比时间不能为空");
        LocalDateTime date = LocalDateTime.parse(checkDateTime, yyyyMMddHHmmss_EN);
        LocalDateTime end = LocalDateTime.parse(dateTime, yyyyMMddHHmmss_EN);
        return date.isEqual(end);
    }

//==================================================================================================

    /**
     * 生日转为年龄，计算法定年龄
     *
     * @param birthDay 生日
     * @return 年龄
     */
    public static long ageOfNow(String birthDay) {
        LocalDate playerDate = LocalDate.from(yyyyMMdd_EN.parse(birthDay));
        return ageOfNow(playerDate);
    }

    /**
     * 生日转为年龄，计算法定年龄
     *
     * @param birthDay 生日
     * @return 年龄
     */
    public static long ageOfNow(LocalDate birthDay) {
        LocalDate today = LocalDate.now();
        long years = ChronoUnit.YEARS.between(birthDay, today);
        return years;
    }


    /**
     * Instant转换为LocalDateTime
     * @param instant
     * @return
     */
    public static LocalDateTime toLocalDateTime(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    /**
     * 获取日期结果集
     * 步进单位，暂不支持
     * @param start 起始日期
     * @param end   结束日期
     *
     */
    public static List<LocalDate> rangeToList(LocalDate start, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        while (start.isBefore(end)) {
            dateList.add(start);
            start = start.plusDays(1);
        }
        return dateList;
    }


    /**
     * 计时，常用于记录某段代码的执行时间，单位：毫秒
     *
     * @param preTime 之前记录的时间
     * @return 时间差，毫秒
     */
    public static long spendMillis(long preTime) {
        return System.currentTimeMillis() - preTime;
    }


    /**
     * 相差详细时间间隔
     */
    public static class TimeInterval {
        private long years;
        private long months;
        private long days;
        private long hours;
        private long minutes;
        private long seconds;
        private long millis;

        public long getYears() {
            return years;
        }

        public TimeInterval setYears(long years) {
            this.years = years;
            return this;
        }

        public long getMonths() {
            return months;
        }

        public TimeInterval setMonths(long months) {
            this.months = months;
            return this;
        }

        public long getDays() {
            return days;
        }

        public TimeInterval setDays(long days) {
            this.days = days;
            return this;
        }

        public long getHours() {
            return hours;
        }

        public TimeInterval setHours(long hours) {
            this.hours = hours;
            return this;
        }

        public long getMinutes() {
            return minutes;
        }

        public TimeInterval setMinutes(long minutes) {
            this.minutes = minutes;
            return this;
        }

        public long getSeconds() {
            return seconds;
        }

        public TimeInterval setSeconds(long seconds) {
            this.seconds = seconds;
            return this;
        }

        public long getMillis() {
            return millis;
        }

        public TimeInterval setMillis(long millis) {
            this.millis = millis;
            return this;
        }

        @Override
        public String toString() {
            return "TimeInterval{" +
                    "years=" + years +
                    ", months=" + months +
                    ", days=" + days +
                    ", hours=" + hours +
                    ", minutes=" + minutes +
                    ", seconds=" + seconds +
                    ", millis=" + millis +
                    '}';
        }
    }

    public static void main(String[] args) {
        LocalDateTime dateTime1 = LocalDateTime.of(2020, 11, 12, 12, 13, 10);
        LocalDateTime dateTime2 = LocalDateTime.of(2021, 12, 20, 11, 13, 11);
        System.out.println(betweenDays(dateTime2, dateTime1));
        System.out.println(between("2021-12-11 12:13:10", "2021-12-12 12:14:10", ChronoUnit.DAYS));
        Duration duration = Duration.between(dateTime1, dateTime2);
        System.out.println(duration.toDays());
        System.out.println(duration.toDays());
        System.out.println(duration.toHours());
        System.out.println(duration.toMinutes());
        System.out.println(duration.toString());
        System.out.println(duration.toMillis());

        System.out.println(duration.get(ChronoUnit.SECONDS));
        System.out.println(between(dateTime1, dateTime2));
        System.out.println(rangeToList(dateTime1.toLocalDate(), dateTime2.toLocalDate()));

    }
}

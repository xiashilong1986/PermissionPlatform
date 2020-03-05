package com.permission.utils.date;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * **********************
 * * Author: XiaShiLong *
 * * Date: 2018-09-21   *
 * * Time: 9:40        *
 * **********************
 **/
public class DateUtils {
    /**
     * 获取两个日期中的所有日期
     *
     * @param beginDate 开始日期
     * @param endDate   结束日期
     * @return List<LocalDate>
     */
    public static List<LocalDate> betweenDate(LocalDate beginDate, LocalDate endDate) {
        int days = Period.between(beginDate, endDate).getDays() + 1;
        return Stream
                .iterate(beginDate, date -> date.plusDays(1))
                .limit(days)
                .collect(Collectors.toList());
    }

    /**
     * 获取中文星期
     *
     * @param dayOfWeek java 8 星期枚举对象 <see>DayOfWeek</see>
     * @return String 星期一 ... 星期日
     */
    public static String chinaWeek(DayOfWeek dayOfWeek) {
        return dayOfWeek.getDisplayName(TextStyle.FULL, Locale.CHINESE);
    }
}

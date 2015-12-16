package com.mricefox.androidhorizontalcalendar.assist;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/11/26
 */
public class CalendarUtil {
    public static final String PATTERN_YYYY_MM_DD = "yyyy-MM-dd";
    /**
     * SimpleDateFormat with pattern yyyy-MM-dd
     */
    private static SimpleDateFormat format;

    private CalendarUtil() {
    }

    static {
        format = new SimpleDateFormat("yyyy-MM-dd");
    }

    /**
     * convert calendar to date string with format yyyy-MM-dd
     *
     * @param calendar
     * @return
     */
    public static String calendar2Str(Calendar calendar) {
        return format.format(calendar.getTime());
    }

    /**
     * convert date string with format yyyy-MM-dd to millisecond
     *
     * @param date
     * @return -1 if exception throw
     */
    public static long convertDateStr2Millis(String date) {
        if (date == null || date.trim().length() == 0)
            return -1;
        try {
            return format.parse(date).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }
}

package com.mricefox.androidhorizontalcalendar.assist;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
    private static SimpleDateFormat formatYYYY_MM_DD;

    /**
     * SimpleDateFormat with pattern yyyyMMdd
     */
    private static SimpleDateFormat formatYYYYMMDD;

    private CalendarUtil() {
    }

    static {
        formatYYYY_MM_DD = new SimpleDateFormat(PATTERN_YYYY_MM_DD);
        formatYYYYMMDD = new SimpleDateFormat("yyyyMMdd");
    }

    /**
     * convert calendar to date string with format yyyy-MM-dd
     *
     * @param calendar
     * @return
     */
    public static String calendar2Str(Calendar calendar) {
        return formatYYYY_MM_DD.format(calendar.getTime());
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
            return formatYYYY_MM_DD.parse(date).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static int getOffsetFirstDayOfWeek(int firstDayOfWeek, int weekDay) {
        int offset = weekDay - firstDayOfWeek;
        return offset >= 0 ? offset : 7 + offset;
    }

    /**
     * get a "clean" calendar as 1900-01-01 00:00:00
     *
     * @return
     */
    public static Calendar getCleanCalendar() {
        long time = convertDateStr2Millis("1900-01-01");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return calendar;
    }

    /**
     * if two date is the same day, for example ,1999-11-21 00:00:00 and  1999-11-21 23:15:40 are in same day,
     * but 1999-11-21 00:00:00 and 2010-11-21 00:00:00 are not
     *
     * @param ltime
     * @param rtime
     * @return
     */
    public static boolean sameDay(long ltime, long rtime) {
        Calendar lcal = Calendar.getInstance();
        Calendar rcal = Calendar.getInstance();
        lcal.setTimeInMillis(ltime);
        rcal.setTimeInMillis(rtime);

        String ldate = calendar2Str(lcal);//yyyy-MM-dd
        String rdate = calendar2Str(rcal);
        return ldate.equals(rdate);
    }

    /**
     * compare two time by date ,ignore HH:mm:ss
     *
     * @param ltime
     * @param rtime
     * @return
     */
    public static int compareDay(long ltime, long rtime) {
        int lValue = Integer.valueOf(formatYYYYMMDD.format(new Date(ltime)));
        int rValue = Integer.valueOf(formatYYYYMMDD.format(new Date(rtime)));

        if (lValue > rValue) {//ascending by YYYYMMDD
            return 1;
        } else if (lValue < rValue) {
            return -1;
        } else {
            return 0;
        }
    }

    /**
     * get month between minTime and maxTime, including head and tail
     *
     * @param minTime
     * @param maxTime
     * @return
     */
    public static int getMonthNum(long minTime, long maxTime) {
        Calendar min = Calendar.getInstance();
        min.setTimeInMillis(minTime);
        Calendar max = Calendar.getInstance();
        max.setTimeInMillis(maxTime);
        if (max.before(min)) {
            throw new IllegalArgumentException("max date is before min date");
        }
        int minMonth = min.get(Calendar.MONTH) + 1;
        int maxMonth = max.get(Calendar.MONTH) + 1;
        return (max.get(Calendar.YEAR) - min.get(Calendar.YEAR)) * 12 + maxMonth - minMonth + 1;
    }


    public static int getWeekday(long time) {
        Calendar min = Calendar.getInstance();
        min.setTimeInMillis(time);
        return min.get(Calendar.DAY_OF_WEEK);
    }
}

package com.mricefox.androidhorizontalcalendar.calendar;

import android.graphics.Color;

import com.mricefox.androidhorizontalcalendar.assist.CalendarUtil;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/11/25
 */
public abstract class AbstractCalendarViewAdapter {
    /**
     * The default minimal date.
     */
    protected static final String DEFAULT_MIN_DATE = "1900-01-01";

    /**
     * The default maximal date.
     */
    protected static final String DEFAULT_MAX_DATE = "2100-01-01";

    protected static long defaultMinDateMillis = CalendarUtil.convertDateStr2Millis(DEFAULT_MIN_DATE);

    protected static long defaultMaxDateMillis = CalendarUtil.convertDateStr2Millis(DEFAULT_MAX_DATE);

    protected static int defaultFirstDayOfWeek = Calendar.getInstance(Locale.getDefault()).getFirstDayOfWeek();

    protected long getMinDateMillis() {
        return defaultMinDateMillis;
    }

    protected long getMaxDateMillis() {
        return defaultMaxDateMillis;
    }

    protected abstract List<CalendarCell> getDataSource();

    protected int getFirstDayOfWeek() {
        return defaultFirstDayOfWeek;
    }

    /**
     * set the normal color of weekend ,caution: this may overlap the color of the data source
     *
     * @return
     */
    protected int getWeekendColor() {
        return Color.RED;
    }
}

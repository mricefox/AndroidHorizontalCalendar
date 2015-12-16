package com.mricefox.androidhorizontalcalendar.calendar;

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
    protected static final String DEFAULT_MIN_DATE = "01/01/1900";

    /**
     * The default maximal date.
     */
    protected static final String DEFAULT_MAX_DATE = "01/01/2100";

    protected long getMinDateMillis() {
        return CalendarUtil.convertDateStr2Millis(DEFAULT_MIN_DATE);
    }

    protected long getMaxDateMillis() {
        return CalendarUtil.convertDateStr2Millis(DEFAULT_MAX_DATE);
    }

    abstract List<CalendarCell> getDataSource();

    protected int getFirstDayOfWeek() {
        return Calendar.getInstance(Locale.getDefault()).getFirstDayOfWeek();
    }
}

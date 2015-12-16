package com.mricefox.androidhorizontalcalendar.calendar;

import java.util.List;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/12/16
 */
public class CalendarViewAdapter extends AbstractCalendarViewAdapter {
    @Override
    long getMinDateMillis() {
        return 0;
    }

    @Override
    long getMaxDateMillis() {
        return 0;
    }

    @Override
    List<CalendarCell> getDataSource() {
        return null;
    }
}

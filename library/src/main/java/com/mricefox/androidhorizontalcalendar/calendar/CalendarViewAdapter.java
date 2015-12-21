package com.mricefox.androidhorizontalcalendar.calendar;

import android.graphics.Color;

import java.util.List;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/12/16
 */
public class CalendarViewAdapter extends AbstractCalendarViewAdapter {
    @Override
    protected long getMinDateMillis() {
        return super.getMinDateMillis();
    }

    @Override
    protected long getMaxDateMillis() {
        return super.getMaxDateMillis();
    }

    @Override
    protected List<AbstractCalendarCell> getDataSource() {
        return null;
    }

    @Override
    protected int getFirstDayOfWeek() {
        return super.getFirstDayOfWeek();
    }

    @Override
    protected int getRowSepLineColor() {
        return Color.DKGRAY;
    }
}

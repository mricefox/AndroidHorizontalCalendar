package com.mricefox.androidhorizontalcalendar.calendar;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/11/26
 */
abstract class AbstractCalendarCell implements CalendarCell {
    protected long dateMillis;

    public AbstractCalendarCell(long dateMillis) {
        this.dateMillis = dateMillis;
    }

    @Override
    public String getHeaderText() {
        return null;
    }

    @Override
    public String getFooterText() {
        return null;
    }

    @Override
    public long getDateMillis() {
        return dateMillis;
    }

    @Override
    public int getDateTextNormalColor() {
        return 0;
    }

    @Override
    public int getDateTextHighlightColor() {
        return 0;
    }

    @Override
    public int getHeaderTextColor() {
        return 0;
    }

    @Override
    public int getFooterTextColor() {
        return 0;
    }
}

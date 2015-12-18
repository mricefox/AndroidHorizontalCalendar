package com.mricefox.androidhorizontalcalendar.calendar;

import android.graphics.Color;

import com.mricefox.androidhorizontalcalendar.assist.CalendarUtil;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/11/26
 */
public abstract class AbstractCalendarCell {
    public static int DEFAULT_DATE_TEXT_COLOR = Color.BLACK;

    protected long dateMillis;
    protected int dateTextNormalColor = DEFAULT_DATE_TEXT_COLOR;

    public AbstractCalendarCell(long dateMillis) {
        this.dateMillis = dateMillis;
    }

    public String getHeaderText() {
        return null;
    }

    public String getFooterText() {
        return null;
    }

    public long getDateMillis() {
        return dateMillis;
    }

    public int getDateTextNormalColor() {
        return dateTextNormalColor;
    }

    public void setDateTextNormalColor(int dateTextNormalColor) {
        this.dateTextNormalColor = dateTextNormalColor;
    }

    public int getDateTextHighlightColor() {
        return 0;
    }

    public int getHeaderTextColor() {
        return 0;
    }

    public int getFooterTextColor() {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof AbstractCalendarCell) {
            //same day cells are equal, no need dateMillis accuracy same
            return CalendarUtil.sameDay(((AbstractCalendarCell) o).dateMillis, dateMillis);
//            return ((AbstractCalendarCell) o).dateMillis == dateMillis;
        } else
            return false;
    }
}

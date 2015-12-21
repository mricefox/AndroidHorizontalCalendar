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
    public static float DEFAULT_DATE_TEXT_SIZE = 50;

    protected long dateMillis;
    protected int dateTextNormalColor = DEFAULT_DATE_TEXT_COLOR;
    protected float dateTextSize = DEFAULT_DATE_TEXT_SIZE;
    protected int dateHighlightColor = Color.WHITE;

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

    public int getHeaderTextColor() {
        return 0;
    }

    public int getFooterTextColor() {
        return 0;
    }

    public float getDateTextSize() {
        return dateTextSize;
    }

    public void setDateTextSize(float dateTextSize) {
        this.dateTextSize = dateTextSize;
    }

    public int getDateHighlightColor() {
        return dateHighlightColor;
    }

    public void setDateHighlightColor(int dateHighlightColor) {
        this.dateHighlightColor = dateHighlightColor;
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

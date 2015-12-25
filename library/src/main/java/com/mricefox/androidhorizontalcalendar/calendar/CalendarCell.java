package com.mricefox.androidhorizontalcalendar.calendar;

import android.graphics.Color;

import com.mricefox.androidhorizontalcalendar.assist.CalendarUtil;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/11/26
 */
public class CalendarCell implements Cloneable {
    public static final int DEFAULT_DATE_TEXT_COLOR = Color.BLACK;
    public static final float DEFAULT_DATE_TEXT_SIZE = 50;
    public static final int AVAILABLE_MODE_MASK = 0x1;
    public static final int AVAILABLE_MODE_SHIFT = 1;

    protected long dateMillis;
    protected int dateTextNormalColor = DEFAULT_DATE_TEXT_COLOR;
    protected float dateTextSize = DEFAULT_DATE_TEXT_SIZE;
    protected int dateHighlightColor = Color.WHITE;
    protected int availableMode = 0x3;

    public CalendarCell(long dateMillis) {
        this.dateMillis = dateMillis;
    }

    public void reset() {
        dateMillis = 0;
        dateTextNormalColor = DEFAULT_DATE_TEXT_COLOR;
        dateTextSize = DEFAULT_DATE_TEXT_SIZE;
        dateHighlightColor = Color.WHITE;
        availableMode = 0x3;
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

    public int getAvailableMode() {
        return availableMode;
    }

    public void setAvailableMode(int availableMode) {
        this.availableMode = availableMode;
    }

    public void setAvailableMode(int auto, int manual) {
        this.availableMode = auto << AVAILABLE_MODE_SHIFT | manual;
    }

    /**
     * date out of month auto set unavailable
     *
     * @return
     */
    public int getAutoAvailable() {
        return (this.availableMode & ~AVAILABLE_MODE_MASK) >> AVAILABLE_MODE_SHIFT;
    }

    /**
     * user set the date unavailable
     *
     * @return
     */
    public int getManualAvailable() {
        return this.availableMode & AVAILABLE_MODE_MASK;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof CalendarCell) {
            //same day cells are equal, no need dateMillis accuracy same
            return CalendarUtil.sameDay(((CalendarCell) o).dateMillis, dateMillis);
//            return ((CalendarCell) o).dateMillis == dateMillis;
        } else
            return false;
    }

    @Override
    protected Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}

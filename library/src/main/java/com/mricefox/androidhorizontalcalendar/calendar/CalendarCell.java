package com.mricefox.androidhorizontalcalendar.calendar;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/11/26
 */
public interface CalendarCell {
    String getHeaderText();

    String getFooterText();

    long getDateMillis();

    int getDateTextNormalColor();

    int getDateTextHighlightColor();

    int getHeaderTextColor();

    int getFooterTextColor();
}

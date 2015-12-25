package com.mricefox.androidhorizontalcalendar.calendar;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.mricefox.androidhorizontalcalendar.assist.CalendarUtil;

import java.util.Calendar;
import java.util.Locale;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/12/25
 */
public abstract class AbsCalendarView<T extends AbsCalendarViewAdapter> extends FrameLayout {
    /**
     * The default minimal date.
     */
    protected static final String DEFAULT_MIN_DATE = "1900-01-01";

    /**
     * The default maximal date.
     */
    protected static final String DEFAULT_MAX_DATE = "2100-01-01";

    /**
     * {@value}
     */
    protected static final int DEFAULT_MAX_HIGHLIGHT_NUM = 0x1;

    protected long minDateMillis = CalendarUtil.convertDateStr2Millis(DEFAULT_MIN_DATE);
    protected long maxDateMillis = CalendarUtil.convertDateStr2Millis(DEFAULT_MAX_DATE);
    protected int firstDayOfWeek = Calendar.getInstance(Locale.getDefault()).getFirstDayOfWeek();
    protected int weekendColor = Color.RED;
    protected int rowSepLineColor = Color.TRANSPARENT;
    protected int maxhighlightNum = DEFAULT_MAX_HIGHLIGHT_NUM;
    protected int highlightColor = Color.BLUE;
    protected int monthCount;

    protected T mAdapter;
    protected OnDateTapListener dateTapListener;


    public AbsCalendarView(Context context) {
        super(context);
    }

    public AbsCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AbsCalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AbsCalendarView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public abstract void setAdapter(T adapter);

    public abstract void scrollToDate(int year, int monthOfYear, int dayOfMonth) throws IllegalArgumentException;

    public void setOnDateTapListener(OnDateTapListener listener) {
        this.dateTapListener = listener;
    }

    public interface OnDateTapListener {
        void onTap(CalendarCell cell);
    }
}

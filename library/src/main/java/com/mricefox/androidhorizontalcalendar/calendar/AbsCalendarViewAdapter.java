package com.mricefox.androidhorizontalcalendar.calendar;

import java.util.List;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/11/25
 */
public abstract class AbsCalendarViewAdapter {
    private final DataSetObservable mDataSetObservable = new DataSetObservable();

    public void registerDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.registerObserver(observer);
    }

    public void unregisterDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.unregisterObserver(observer);
    }

    /**
     * Notifies the attached observers that the underlying data has been changed
     * and any View reflecting the data set should refresh itself.
     */
    public void notifyDataSetChanged() {
        mDataSetObservable.notifyChanged();
    }

    public void notifyItemRangeChanged(long from, long to) {
        mDataSetObservable.notifyItemRangeChanged(from, to);
    }
//    protected static final int MAX_HIGHLIGHT_NUM = 0x1;
//
//    protected static long defaultMinDateMillis = CalendarUtil.convertDateStr2Millis(DEFAULT_MIN_DATE);
//
//    protected static long defaultMaxDateMillis = CalendarUtil.convertDateStr2Millis(DEFAULT_MAX_DATE);
//
//    protected static int defaultFirstDayOfWeek = Calendar.getInstance(Locale.getDefault()).getFirstDayOfWeek();
//
//    protected long getMinDateMillis() {
//        return defaultMinDateMillis;
//    }
//
//    protected long getMaxDateMillis() {
//        return defaultMaxDateMillis;
//    }

    protected abstract List<CalendarCell> getDataSource();

//    protected int getFirstDayOfWeek() {
//        return defaultFirstDayOfWeek;
//    }

    /**
     * set the normal color of weekend ,caution: this may overlap the color of the data source
     *
     * @return
     */
//    protected int getWeekendColor() {
//        return Color.RED;
//    }
//
//    protected int getRowSepLineColor() {
//        return Color.TRANSPARENT;
//    }
//
//    protected int getHighlightColor() {
//        return Color.BLUE;
//    }
//
//    protected int getMaxHighlightNum() {
//        return MAX_HIGHLIGHT_NUM;
//    }
}

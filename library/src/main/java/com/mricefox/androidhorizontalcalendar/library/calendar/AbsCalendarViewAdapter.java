package com.mricefox.androidhorizontalcalendar.library.calendar;

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

    protected abstract List<CalendarCell> getDataSource();
}

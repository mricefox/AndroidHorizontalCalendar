package com.mricefox.androidhorizontalcalendar.library.calendar;

import android.database.Observable;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/12/25
 */
public class DataSetObservable extends Observable<DataSetObserver> {
    public boolean hasObservers() {
        synchronized (mObservers) {
            return !mObservers.isEmpty();
        }
    }

    public void notifyChanged() {
        synchronized (mObservers) {//mObservers register and notify maybe in different thread
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onChanged();
            }
        }
    }

    public void notifyItemRangeChanged(long from, long to) {
        synchronized (mObservers) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onItemRangeChanged(from, to);
            }
        }
    }
}

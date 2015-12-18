package com.mricefox.androidhorizontalcalendar.calendar;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mricefox.androidhorizontalcalendar.assist.MFLog;

import java.util.Calendar;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/11/25
 */
public class HorizontalCalendarView extends LinearLayout {
    private View headerView;
    private ViewPager monthViewPage;
    private PagerAdapter monthPageAdapter;
    private Context context;
    private CalendarViewAdapter calendarViewAdapter;

    public HorizontalCalendarView(Context context) {
        super(context);
        init(context);
    }

    public HorizontalCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public HorizontalCalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HorizontalCalendarView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        setupViewPager();
        if (!viewAlreadyAdded(monthViewPage)) {
            addView(monthViewPage);
        }
    }

    // TODO: 2015/11/25  CalendarViewAdapter has not initialized
    private void setupViewPager() {
        if (monthViewPage == null) {
            monthViewPage = new ViewPager(context);
        }
    }

    public void setAdapter(CalendarViewAdapter adapter) {
        if (calendarViewAdapter == adapter)
            return;
        calendarViewAdapter = adapter;
        final int monthNum = getMonthNum(calendarViewAdapter.getMinDateMillis(),
                calendarViewAdapter.getMaxDateMillis());
        monthPageAdapter = new PagerAdapter() {
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                MFLog.d("instantiateItem position:" + position);
                MonthView monthView = new MonthView(context);
                Calendar calendar = getCurrentMonth(calendarViewAdapter.getMinDateMillis(), position);
//                MFLog.d("calendar :" + calendar.getTimeInMillis());
                monthView.initData(calendar, calendarViewAdapter.getDataSource(), calendarViewAdapter.getFirstDayOfWeek());
                container.addView(monthView);
                return monthView;
            }

            @Override
            public int getCount() {
                return monthNum;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
//                super.destroyItem(container, position, object);
                container.removeView((View) object);
            }
        };
        monthViewPage.setAdapter(monthPageAdapter);
    }

    private boolean viewAlreadyAdded(View v) {
        int size = getChildCount();
        boolean exists = false;
        if (size > 0) {
            for (int i = 0; i < size; ++i) {
                View child = getChildAt(i);
                exists |= child == v;
            }
        }
        return exists;
    }

    private int getMonthNum(long minTime, long maxTime) {
        Calendar min = Calendar.getInstance();
        min.setTimeInMillis(minTime);
        Calendar max = Calendar.getInstance();
        max.setTimeInMillis(maxTime);
        if (max.before(min)) {
            throw new IllegalArgumentException("max date is before min date");
        }
        int minMonth = min.get(Calendar.MONTH) + 1;
        int maxMonth = max.get(Calendar.MONTH) + 1;
        return (max.get(Calendar.YEAR) - min.get(Calendar.YEAR)) * 12 + maxMonth - minMonth + 1;
    }

    private Calendar getCurrentMonth(long minTimeMillis, int position) {
        Calendar min = Calendar.getInstance();
        min.setTimeInMillis(minTimeMillis);
        int minMonthOfYear = min.get(Calendar.MONTH) + 1;
        Calendar monthCal = Calendar.getInstance();//show month // TODO: 2015/12/18 clean day
        int n = position + minMonthOfYear - 12;
        int yearOffset = (int) Math.ceil(n / 12.0f);
        monthCal.set(Calendar.YEAR, min.get(Calendar.YEAR) + yearOffset);
        monthCal.set(Calendar.MONTH, n > 0 ? n % 12 : position + minMonthOfYear - 1);
        return monthCal;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }
}

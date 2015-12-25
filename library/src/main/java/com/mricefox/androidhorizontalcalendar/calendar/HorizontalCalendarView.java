package com.mricefox.androidhorizontalcalendar.calendar;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mricefox.androidhorizontalcalendar.assist.CalendarUtil;
import com.mricefox.androidhorizontalcalendar.assist.MFLog;
import com.mricefox.androidhorizontalcalendar.library.R;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/11/25
 */
public class HorizontalCalendarView extends AbsCalendarView implements MonthView.OnTapOutOfMonthListener {
    private ViewPager monthViewPage;
    private MonthPagerAdapter monthPageAdapter;
    private TextView monthTxt;

    private HorizontalObserver mObserver;

    public HorizontalCalendarView(Context context) {
        super(context);
        initialize();
    }

    public HorizontalCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public HorizontalCalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HorizontalCalendarView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize();
    }

    private void initialize() {
        monthCount = CalendarUtil.getMonthNum(minDateMillis, maxDateMillis);
        mObserver = new HorizontalObserver();

        LayoutInflater layoutInflater = (LayoutInflater) getContext()
                .getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        View content = layoutInflater.inflate(R.layout.calendar_view, null, false);
        addView(content);

        monthViewPage = (ViewPager) content.findViewById(R.id.month_viewpager);
        monthPageAdapter = new MonthPagerAdapter();
        monthViewPage.setAdapter(monthPageAdapter);
        monthViewPage.addOnPageChangeListener(new MonthPageChangeListener());

        setupHeader(content);
        initMonthTxt(content);
    }

    @Override
    public void onTapOut(CalendarCell cell, int tailOrHead) {
        int current = monthViewPage.getCurrentItem();
        int total = monthPageAdapter.getCount();
        if (tailOrHead == MonthView.TAP_BEFORE_HEAD) {
            if (current > 0)
                monthViewPage.setCurrentItem(current - 1, true);
        } else if (tailOrHead == MonthView.TAP_AFTER_TAIL) {
            if (current < total - 1)
                monthViewPage.setCurrentItem(current + 1, true);
        }
    }

    @Override
    public void setAdapter(AbsCalendarViewAdapter adapter) {
        if (mAdapter == adapter)
            return;
        if (mAdapter != null && mObserver != null) {
            mAdapter.unregisterDataSetObserver(mObserver);
        }
        mAdapter = adapter;

        if (mAdapter != null) {
            mAdapter.registerDataSetObserver(mObserver);
        }
//        monthPageAdapter.notifyDataSetChanged();
    }

    private class HorizontalObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            super.onChanged();


        }

        @Override
        public void onItemRangeChanged(long from, long to) {
            super.onItemRangeChanged(from, to);
        }
    }

    private class MonthPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            MFLog.d("onPageSelected pos:" + position);
            // TODO: 2015/12/21 select the current day of month corresponding previous month highlight date
            Calendar c = getMonthByPosition(minDateMillis, position);
            String date = CalendarUtil.calendar2Str(c);
            monthTxt.setText(date.substring(0, date.lastIndexOf("-")));
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }

    private class MonthPagerAdapter extends PagerAdapter {
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
//            return super.instantiateItem(container, position);
            MonthView monthView = new MonthView(getContext());
            Calendar calendar = getMonthByPosition(minDateMillis, position);
            List<CalendarCell> data = mAdapter == null ? null : mAdapter.getDataSource();
            monthView.initData(calendar, data, firstDayOfWeek, weekendColor,
                    rowSepLineColor, dateTapListener, highlightColor, maxhighlightNum,
                    HorizontalCalendarView.this);
            container.addView(monthView);
            return monthView;
        }

        @Override
        public int getCount() {
            return monthCount;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
//            super.destroyItem(container, position, object);
//            monthViewPage.clearOnPageChangeListeners();
            container.removeView((View) object);
        }
    }

//    private void dummy(){
//        mAdapter = new AbsCalendarViewAdapter() {
//            @Override
//            protected List<CalendarCell> getDataSource() {
//                List<CalendarCell> cells = new ArrayList<>();
//                long start = CalendarUtil.convertDateStr2Millis("1900-01-01");
//
//                for (long i = 0; i < 37; ++i) {//dummy data
//                    CalendarCell cell = new CalendarCell(start + 86400000L * i);
//                    cell.setDateTextNormalColor(Color.BLUE);
////            if (i == 4 || i == 7 || i == 35) {
////                cell.setAvailableMode(1, 0);
////            }
//                    cells.add(cell);
//                }
//
//                return cells;
//            }
//        };
//    }

    @Override
    public void scrollToDate(int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear - 1);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        // TODO: 2015/12/24  invalid date
        int pos = getPositionByCalendar(calendar, minDateMillis);
        monthViewPage.setCurrentItem(pos, true);
    }

    /**
     * setup header to show week day
     *
     * @param content
     */
    private void setupHeader(View content) {
        DateFormatSymbols symbols = DateFormatSymbols.getInstance();
        String[] weekdays = symbols.getShortWeekdays();
        TextView[] textViewArr = {
                (TextView) content.findViewById(R.id.weekday_1),
                (TextView) content.findViewById(R.id.weekday_2),
                (TextView) content.findViewById(R.id.weekday_3),
                (TextView) content.findViewById(R.id.weekday_4),
                (TextView) content.findViewById(R.id.weekday_5),
                (TextView) content.findViewById(R.id.weekday_6),
                (TextView) content.findViewById(R.id.weekday_7),
        };
        int index = 0;
        for (int i = firstDayOfWeek; i <= Calendar.SATURDAY; ++i, ++index) {
            textViewArr[index].setText(weekdays[i]);
        }
        if (firstDayOfWeek > Calendar.SUNDAY) {
            for (int i = Calendar.SUNDAY; i <= firstDayOfWeek - 1; ++i, ++index) {
                textViewArr[index].setText(weekdays[i]);
            }
        }
    }

    private void initMonthTxt(View content) {
        monthTxt = (TextView) content.findViewById(R.id.month_txt);
        Calendar c = getMonthByPosition(minDateMillis, 0);
        String date = CalendarUtil.calendar2Str(c);
        monthTxt.setText(date.substring(0, date.lastIndexOf("-")));//1900-01
    }

    private Calendar getMonthByPosition(long minTimeMillis, int position) {
        Calendar min = Calendar.getInstance();
        min.setTimeInMillis(minTimeMillis);
        int minMonthOfYear = min.get(Calendar.MONTH) + 1;
        Calendar monthCal = Calendar.getInstance();//show month // TODO: 2015/12/18 clean day
        int n = position + minMonthOfYear - 12;
        int yearOffset = (int) Math.ceil(n / 12.0f);
        monthCal.set(Calendar.YEAR, min.get(Calendar.YEAR) + yearOffset);
        int month;
        if (n > 0) {
            month = n % 12 == 0 ? Calendar.DECEMBER : n % 12 - 1;
        } else {
            month = position + minMonthOfYear - 1;
        }
        monthCal.set(Calendar.MONTH, month);
        return monthCal;
    }

    private int getPositionByCalendar(Calendar calendar, long minTimeMillis) {
        Calendar min = Calendar.getInstance();
        min.setTimeInMillis(minTimeMillis);
        int minMonthOfYear = min.get(Calendar.MONTH) + 1;
        int monthOfYear = calendar.get(Calendar.MONTH) + 1;
        int yearOffset = calendar.get(Calendar.YEAR) - min.get(Calendar.YEAR);
        return yearOffset * 12 + monthOfYear - minMonthOfYear;
    }
}

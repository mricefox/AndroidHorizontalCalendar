package com.mricefox.androidhorizontalcalendar.calendar;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.mricefox.androidhorizontalcalendar.assist.CalendarUtil;
import com.mricefox.androidhorizontalcalendar.assist.MFLog;
import com.mricefox.androidhorizontalcalendar.library.R;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.List;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/11/25
 */
public class HorizontalCalendarView extends FrameLayout implements MonthView.OnTapOutOfMonthListener {
    private ViewPager monthViewPage;
    private PagerAdapter monthPageAdapter;
    private AbstractCalendarViewAdapter calendarViewAdapter;
    private OnDateTapListener dateTapListener;
    private long minDateMillis;

    public HorizontalCalendarView(Context context) {
        super(context);
    }

    public HorizontalCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public HorizontalCalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HorizontalCalendarView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onTapOut(AbstractCalendarCell cell, int tailOrHead) {
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

    public interface OnDateTapListener {
        void onTap(AbstractCalendarCell cell);
    }

    public void setOnDateTapListener(OnDateTapListener listener) {
        this.dateTapListener = listener;
    }

    public void setAdapter(AbstractCalendarViewAdapter adapter) {
        if (calendarViewAdapter == adapter)
            return;
        calendarViewAdapter = adapter;
        final long minDateMillis = calendarViewAdapter.getMinDateMillis();

        this.minDateMillis = minDateMillis;

        final long maxDateMillis = calendarViewAdapter.getMaxDateMillis();
        final int monthNum = CalendarUtil.getMonthNum(minDateMillis, maxDateMillis);
        final List<AbstractCalendarCell> data = calendarViewAdapter.getDataSource();
        final int firstDayOfWeek = calendarViewAdapter.getFirstDayOfWeek();
        final int weekendColor = calendarViewAdapter.getWeekendColor();
        final int rowSepLineColor = calendarViewAdapter.getRowSepLineColor();
        final int highlightColor = calendarViewAdapter.getHighlightColor();
        final int maxhighlightNum = calendarViewAdapter.getMaxHighlightNum();
        monthPageAdapter = new PagerAdapter() {
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                MFLog.d("instantiateItem position:" + position);
                MonthView monthView = new MonthView(getContext());
                Calendar calendar = getMonthByPosition(minDateMillis, position);
                monthView.initData(calendar, data, firstDayOfWeek, weekendColor,
                        rowSepLineColor, dateTapListener, highlightColor, maxhighlightNum,
                        HorizontalCalendarView.this);
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
                MFLog.d("destroyItem position:" + position);
            }
        };
        LayoutInflater layoutInflater = (LayoutInflater) getContext()
                .getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        View content = layoutInflater.inflate(R.layout.calendar_view, null, false);
        addView(content);
        monthViewPage = (ViewPager) content.findViewById(R.id.month_viewpager);
        monthViewPage.setAdapter(monthPageAdapter);

//        View header = (LinearLayout) content.findViewById(R.id.header_view);
        setupHeader(content);
        final TextView monthTxt = (TextView) content.findViewById(R.id.month_txt);

        monthViewPage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
        });
    }

    public void scrollToDate(int year, int monthOfYear, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, monthOfYear);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        // TODO: 2015/12/24  invalid date
        int pos = getPositionByCalendar(c, minDateMillis);
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
        int firstDayOfWeek = calendarViewAdapter.getFirstDayOfWeek();
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

    private Calendar getMonthByPosition(long minTimeMillis, int position) {
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

    private int getPositionByCalendar(Calendar calendar, long minTimeMillis) {
        Calendar min = Calendar.getInstance();
        min.setTimeInMillis(minTimeMillis);
        int minMonthOfYear = min.get(Calendar.MONTH) + 1;
        int monthOfYear = calendar.get(Calendar.MONTH) + 1;
        int yearOffset = calendar.get(Calendar.YEAR) - min.get(Calendar.YEAR);
        return yearOffset * 12 + monthOfYear - minMonthOfYear;
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

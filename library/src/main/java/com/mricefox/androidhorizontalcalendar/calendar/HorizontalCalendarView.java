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
import android.widget.LinearLayout;
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
public class HorizontalCalendarView extends FrameLayout {
    private LinearLayout header;
    private ViewPager monthViewPage;
    private PagerAdapter monthPageAdapter;
    private AbstractCalendarViewAdapter calendarViewAdapter;
    private OnDateTapListener dateTapListener;

    /**
     * @see Calendar#SUNDAY
     */
//    private int firstDayOfWeek;
    public HorizontalCalendarView(Context context) {
        super(context);
//        init(context);
    }

    public HorizontalCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
//        init(context);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public HorizontalCalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HorizontalCalendarView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
//        init(context);
    }

//    private void init(Context context) {
//        this.context = context;
//    }

    // TODO: 2015/11/25  CalendarViewAdapter has not initialized
//    private void setupViewPager() {
//        monthViewPage = new ViewPager(getContext());
//    }

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
        final int monthNum = CalendarUtil.getMonthNum(calendarViewAdapter.getMinDateMillis(),
                calendarViewAdapter.getMaxDateMillis());
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
                Calendar calendar = getCurrentMonth(calendarViewAdapter.getMinDateMillis(), position);
//                firstDayOfWeek = calendarViewAdapter.getFirstDayOfWeek();
                monthView.initData(calendar, data, firstDayOfWeek, weekendColor,
                        rowSepLineColor, dateTapListener, highlightColor, maxhighlightNum);
                container.addView(monthView);
                MFLog.d("addview " + monthView);
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
//        setupViewPager();
        monthViewPage.setAdapter(monthPageAdapter);
//        if (!viewAlreadyAdded(monthViewPage)) {
//            addView(monthViewPage,
//                    new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));
//        }
        header = (LinearLayout) content.findViewById(R.id.header_view);
        setupHeader(content);
//        if (!viewAlreadyAdded(header)) {
//            //add above monthViewPage
//            addView(header, 0,
//                    new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//        }
        monthViewPage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                MFLog.d("onPageSelected pos:" + position);
                // TODO: 2015/12/21 select the current day of month corresponding previous month highlight date
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

//    private boolean viewAlreadyAdded(View v) {
//        int size = getChildCount();
//        boolean exists = false;
//        if (size > 0) {
//            for (int i = 0; i < size; ++i) {
//                View child = getChildAt(i);
//                exists |= child == v;
//            }
//        }
//        return exists;
//    }

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
//        MFLog.d("index:" + index);
        if (firstDayOfWeek > Calendar.SUNDAY) {
            for (int i = Calendar.SUNDAY; i <= firstDayOfWeek - 1; ++i, ++index) {
                textViewArr[index].setText(weekdays[i]);
            }
        }
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

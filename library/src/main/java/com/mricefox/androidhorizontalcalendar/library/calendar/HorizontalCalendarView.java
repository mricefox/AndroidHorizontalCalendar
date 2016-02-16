package com.mricefox.androidhorizontalcalendar.library.calendar;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mricefox.androidhorizontalcalendar.library.R;
import com.mricefox.androidhorizontalcalendar.library.assist.CalendarUtil;
import com.mricefox.androidhorizontalcalendar.library.assist.MFLog;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/11/25
 */
public class HorizontalCalendarView extends AbsCalendarView {
    private static final int PREV_BUTTON_TAG = 1;
    private static final int NEXT_BUTTON_TAG = 0;

    private ViewPager monthViewPage;
    private MonthPagerAdapter monthPageAdapter;
    private TextView monthTxt;
    private ImageButton prevBtn, nextBtn;
    private HorizontalObserver mObserver;
    private LinkedList<Integer> alivePages;
    /**
     * auto scroll page when tap out of month
     */
    private boolean autoScroll = true;


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

    /**
     * initialize, invoked after constructor
     */
    private void initialize() {
        monthCount = CalendarUtil.getMonthNum(minDateMillis, maxDateMillis);

        LayoutInflater layoutInflater = (LayoutInflater) getContext()
                .getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        View content = layoutInflater.inflate(R.layout.calendar_view, null, false);
        addView(content);

        alivePages = new LinkedList();//alive positions in viewpager
        monthViewPage = (ViewPager) content.findViewById(R.id.mf_horizontal_calendar_month_viewpager);
        monthPageAdapter = new MonthPagerAdapter();
        monthViewPage.setAdapter(monthPageAdapter);
        monthViewPage.addOnPageChangeListener(new MonthPageChangeListener());

        setupHeader(content);
        setupMonthTxt(content);
        setupPageButton(content);
    }

    private void onTapOut(CalendarCell cell, int tailOrHead) {
        if (tailOrHead == MonthView.TAP_BEFORE_HEAD) {
            scrollToPrevMonth();
        } else if (tailOrHead == MonthView.TAP_AFTER_TAIL) {
            scrollToNextMonth();
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
            if (mObserver == null) {
                mObserver = new HorizontalObserver();
            }
            mAdapter.registerDataSetObserver(mObserver);
        }
    }

    private class HorizontalObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            for (int i = alivePages.size() - 1; i >= 0; --i) {
                MonthView mv = (MonthView) monthViewPage.getChildAt(i);//i indicate the real position of child view
                mv.notifyChange();
            }
        }

        @Override
        public void onItemRangeChanged(long from, long to) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(from);
            final int start = getPositionByCalendar(calendar);
            calendar.setTimeInMillis(to);
            final int end = getPositionByCalendar(calendar);

            for (int i = alivePages.size() - 1; i >= 0; --i) {
                final int index = alivePages.get(i);
                if (index >= start && index <= end) {
                    MonthView mv = (MonthView) monthViewPage.getChildAt(i);//i indicate the real position of child view
                    mv.notifyChange();
                }
            }
        }
    }

    private class MonthPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
//            MFLog.d("onPageSelected pos:" + position);
            Calendar c = getMonthByPosition(position);
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
//            MFLog.d("MonthPagerAdapter init:" + position);

            Calendar calendar = getMonthByPosition(position);
            MonthView monthView = new MonthView(getContext(), calendar);
            container.addView(monthView);

            alivePages.offerLast(position);

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
//            MFLog.d("MonthPagerAdapter destroyItem:" + position);
            container.removeView((View) object);
            alivePages.removeFirstOccurrence(position);
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
            /**
             * fix viewpager notifydatasetchange but considerable overhead
             */
//            return POSITION_NONE;
        }
    }

    @Override
    public void scrollToMonth(int year, int monthOfYear) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        if (calendar.getTimeInMillis() < minDateMillis || calendar.getTimeInMillis() > maxDateMillis) {
            throw new IllegalArgumentException("Illegal date:" + year + "/" + monthOfYear);
        }
        int pos = getPositionByCalendar(calendar);
        if (pos != monthViewPage.getCurrentItem()) {
            monthViewPage.setCurrentItem(pos, false);
        }
    }

    @Override
    public void scrollToPrevMonth() {
        int curPos = monthViewPage.getCurrentItem();
        if (curPos > 0) {
            monthViewPage.setCurrentItem(--curPos, true);
        }
    }

    @Override
    public void scrollToNextMonth() {
        int curPos = monthViewPage.getCurrentItem();
        if (curPos < monthCount - 1) {
            monthViewPage.setCurrentItem(++curPos, true);
        }
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
                (TextView) content.findViewById(R.id.mf_horizontal_calendar_weekday_1),
                (TextView) content.findViewById(R.id.mf_horizontal_calendar_weekday_2),
                (TextView) content.findViewById(R.id.mf_horizontal_calendar_weekday_3),
                (TextView) content.findViewById(R.id.mf_horizontal_calendar_weekday_4),
                (TextView) content.findViewById(R.id.mf_horizontal_calendar_weekday_5),
                (TextView) content.findViewById(R.id.mf_horizontal_calendar_weekday_6),
                (TextView) content.findViewById(R.id.mf_horizontal_calendar_weekday_7),
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

    private void setupMonthTxt(View content) {
        monthTxt = (TextView) content.findViewById(R.id.mf_horizontal_calendar_month_txt);
        Calendar c = getMonthByPosition(0);
        String date = CalendarUtil.calendar2Str(c);
        monthTxt.setText(date.substring(0, date.lastIndexOf("-")));//1900-01
    }

    private void setupPageButton(View content) {
        nextBtn = (ImageButton) content.findViewById(R.id.mf_horizontal_calendar_next_btn);
        prevBtn = (ImageButton) content.findViewById(R.id.mf_horizontal_calendar_prev_btn);

        nextBtn.setTag(NEXT_BUTTON_TAG);
        prevBtn.setTag(PREV_BUTTON_TAG);
        prevBtn.setOnClickListener(pageButtonsOnClickListener);
        nextBtn.setOnClickListener(pageButtonsOnClickListener);
    }

    private Calendar getMonthByPosition(long minTimeMillis, int position) {
        Calendar min = Calendar.getInstance();
        min.setTimeInMillis(minTimeMillis);
        int minMonthOfYear = min.get(Calendar.MONTH) + 1;
        Calendar monthCal = CalendarUtil.getCleanCalendar();
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

    public Calendar getMonthByPosition(int position) {
        return getMonthByPosition(minDateMillis, position);
    }

    private int getPositionByCalendar(Calendar calendar, long minTimeMillis) {
        Calendar min = Calendar.getInstance();
        min.setTimeInMillis(minTimeMillis);
        int minMonthOfYear = min.get(Calendar.MONTH) + 1;
        int monthOfYear = calendar.get(Calendar.MONTH) + 1;
        int yearOffset = calendar.get(Calendar.YEAR) - min.get(Calendar.YEAR);
        return yearOffset * 12 + monthOfYear - minMonthOfYear;
    }

    public int getPositionByCalendar(Calendar calendar) {
        return getPositionByCalendar(calendar, minDateMillis);
    }

    /**
     * return a calendar of 1st day of month ,time 00:00:00
     *
     * @return
     */
    public Calendar getCurrentMonth() {
        int curPos = monthViewPage.getCurrentItem();
        return getMonthByPosition(curPos);
    }

    private static Comparator<CalendarCell> cellComparator = new Comparator<CalendarCell>() {
        @Override
        public int compare(CalendarCell lhs, CalendarCell rhs) {
            if (lhs == null && rhs != null) {//null in tail
                return 1;
            } else if (lhs != null && rhs == null) {
                return -1;
            } else if (lhs == null && rhs == null) {
                return 0;
            } else {
                return CalendarUtil.compareDay(lhs.dateMillis, rhs.dateMillis);
            }
        }
    };

    private OnClickListener pageButtonsOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch ((Integer) v.getTag()) {
                case NEXT_BUTTON_TAG:
                    scrollToNextMonth();
                    break;
                case PREV_BUTTON_TAG:
                    scrollToPrevMonth();
                    break;
            }
        }
    };

    /**
     * Author:zengzifeng email:zeng163mail@163.com
     * Description:
     * Date:2015/11/26
     */
    private class MonthView extends View {
        private final static int ROW_NUM = 0x6;
        private final static int DAYS_PER_WEEK = 0x7;
        private static final long MILLIS_IN_DAY = 86400000L;
        public final static int TAP_BEFORE_HEAD = 0x1;
        public final static int TAP_AFTER_TAIL = 0x2;

        private List<CalendarCell> internalCellList;
        private long firstDayMillis, lastDayMillis;
        private long monthFirstDayMillis, monthLastDayMillis;

        private Paint datePaint;
        private Paint footerPaint;
        private Paint linePaint;
        private Paint bgPaint;
        private int vWidth, vHeight;
        private Calendar paintCalendar;
        private PointF cellSize;
        private RectF cellArea;

        private GestureDetector gestureDetector;
        private HorizontalCalendarView.OnDateTapListener dateTapListener;
        private LinkedList<RectF> highlightAreas;

        public MonthView(Context context) {
            super(context);
        }

        public MonthView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public MonthView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public MonthView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        public MonthView(Context context, Calendar calendar) {
            super(context);
            initialize();
            setupData(calendar);
        }

        private void setupData(Calendar monthCal) {
            Calendar firstDayOfMonth = (Calendar) monthCal.clone();
            firstDayOfMonth.set(Calendar.DAY_OF_MONTH, 1);
            final int f_weekDay = firstDayOfMonth.get(Calendar.DAY_OF_WEEK);//the week day of first day of the month
            final int headDayNum = CalendarUtil.getOffsetFirstDayOfWeek(firstDayOfWeek, f_weekDay);
            monthFirstDayMillis = firstDayOfMonth.getTimeInMillis();
            firstDayMillis = monthFirstDayMillis - headDayNum * MILLIS_IN_DAY;
            lastDayMillis = monthFirstDayMillis + (ROW_NUM * DAYS_PER_WEEK - 1) * MILLIS_IN_DAY;

            Calendar lastDayOfMonth = (Calendar) monthCal.clone();
            lastDayOfMonth.set(Calendar.DAY_OF_MONTH, lastDayOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH));
            monthLastDayMillis = lastDayOfMonth.getTimeInMillis();

            setupInternalCellList(mAdapter == null ? null : mAdapter.getDataSource());
        }

        /**
         * get specified month data from total data, including the extra week day that is not in the month
         *
         * @param
         * @return
         */
        private void setupInternalCellList(List<CalendarCell> sourceData) {
            for (int i = 0; i < ROW_NUM * DAYS_PER_WEEK; ++i) {
                long time = firstDayMillis + i * MILLIS_IN_DAY;
                CalendarCell cell = new CalendarCell(time);//generate new cell
                internalCellList.add(generateCell(time, cell, sourceData));
                //setup weekend color
                int weekDay = CalendarUtil.getWeekday(cell.getDateMillis());
                if (weekDay == Calendar.SUNDAY || weekDay == Calendar.SATURDAY) {
                    cell.setDateTextNormalColor(weekendColor);
                }
            }
        }

        private CalendarCell generateCell(long time, CalendarCell cell, List<CalendarCell> sourceData) {
            //date before the first day of this month or after the last day of month, set unavailable
            int auto = outOfMonth(time) > 0 ? 0 : 1;
            CalendarCell source = searchCell(sourceData, cell, true);
            if (source != null) {//find so use source data
                int manual = source.getManualAvailable();
                source.setAvailableMode(auto, manual);
                return source;
            } else {//not found use auto-generate data
                cell.setAvailableMode(auto, 1);
                return cell;
            }
        }

        private CalendarCell searchCell(List<CalendarCell> sourceData, CalendarCell key, boolean copy) {
            int index;
            CalendarCell result = null;

            if (sourceData != null &&
                    (index = Collections.binarySearch(sourceData, key, cellComparator)) >= 0) {
                result = sourceData.get(index);
                if (copy) {
                    //copy source data because month tail or head date may exists in different month view
                    result = (CalendarCell) result.clone();
                }
            }
            return result;
        }

        private void initialize() {
            internalCellList = new ArrayList(ROW_NUM * DAYS_PER_WEEK);

            paintCalendar = Calendar.getInstance();
            cellSize = new PointF();
            cellArea = new RectF();

            bgPaint = new Paint();
            bgPaint.setAntiAlias(true);
            bgPaint.setStyle(Paint.Style.FILL);

            datePaint = new Paint();
            datePaint.setAntiAlias(true);
            datePaint.setStyle(Paint.Style.STROKE);
            datePaint.setTextAlign(Paint.Align.CENTER);

            footerPaint = new Paint();
            footerPaint.setAntiAlias(true);
            footerPaint.setTextAlign(Paint.Align.CENTER);

            linePaint = new Paint();
            linePaint.setAntiAlias(true);

            gestureDetector = new GestureDetector(getContext(), gestureListener);
        }

        private void notifyChange() {
            internalCellList.clear();
            setupInternalCellList(mAdapter == null ? null : mAdapter.getDataSource());
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            long time = System.currentTimeMillis();
            drawCellBackground(canvas);
            drawDays(canvas);
            if (rowSepLineColor != Color.TRANSPARENT)
                drawRowSepLines(canvas);
            MFLog.d(this + "monthview onDraw time:" + (System.currentTimeMillis() - time));
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
//            MFLog.d(this + "monthview onSizeChanged h:" + h + " w:" + w);
            vHeight = h;
            vWidth = w;
            cellSize.set((vWidth + 0f) / DAYS_PER_WEEK, (vHeight + 0f) / ROW_NUM);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            gestureDetector.onTouchEvent(event);
//        return super.onTouchEvent(event);
            return true;
        }

        private void drawCellBackground(Canvas canvas) {
            if (highlightAreas != null && !highlightAreas.isEmpty()) {
                bgPaint.setColor(highlightColor);
                for (RectF r : highlightAreas) {
                    canvas.drawRect(r, bgPaint);
                    // TODO: 2015/12/21 draw circle background
//                canvas.drawCircle(r.centerX(), r.centerY(), 10, bgPaint);
                }
            }
        }

        /**
         * Draw days
         *
         * @param canvas
         */
        private void drawDays(Canvas canvas) {
            for (int row = 0; row < ROW_NUM; ++row) {
                for (int column = 0; column < DAYS_PER_WEEK; ++column) {
                    cellArea.set(cellSize.x * column, cellSize.y * row,
                            cellSize.x * (column + 1), cellSize.y * (row + 1));
                    CalendarCell cell = internalCellList.get(row * DAYS_PER_WEEK + column);
                    drawDateCell(canvas, cell, cellArea);
                    String footerTxt = cell.getFooterTxt();
                    if (footerTxt != null && footerTxt.trim().length() > 0) {
                        drawFooter(canvas, cell, cellArea);
                    }
                }
            }
        }

        /**
         * draw one day cell
         *
         * @param canvas
         * @param cell
         * @param loc
         */
        private void drawDateCell(Canvas canvas, CalendarCell cell, RectF loc) {
            paintCalendar.setTimeInMillis(cell.getDateMillis());
            String dateTxt = String.valueOf((paintCalendar.get(Calendar.DAY_OF_MONTH)));
            //draw date
            if (highlightAreas != null && highlightAreas.contains(loc)) {
                datePaint.setColor(cell.getDateHighlightColor());//highlight color
            } else {
                datePaint.setColor(cell.getDateTextNormalColor());
            }
            if (cell.getAutoAvailable() == 0) {
                datePaint.setColor(Color.GRAY);
            }
            if (cell.getManualAvailable() == 0) {
                /**
                 * manual set unavailable color wrap auto unavailable color
                 */
                datePaint.setColor(Color.GREEN);// TODO: 2015/12/28 manual unavailable color
            }
            datePaint.setTextSize(cell.getDateTextSize());
            float typefaceOffset = -datePaint.ascent() / 2;//recommended height
            canvas.drawText(dateTxt, loc.centerX(), loc.centerY() + typefaceOffset, datePaint);
        }

        private void drawFooter(Canvas canvas, CalendarCell cell, RectF loc) {
            footerPaint.setTextSize(cell.getFooterTxtSize());
            if (highlightAreas != null && highlightAreas.contains(loc)) {
                footerPaint.setColor(cell.getDateHighlightColor());
            } else {
                footerPaint.setColor(cell.getFooterTxtColor());
            }
            canvas.drawText(cell.getFooterTxt(), loc.centerX(), loc.bottom - footerPaint.descent(), footerPaint);
        }

        private void drawRowSepLines(Canvas canvas) {
            float[] linePoints = new float[ROW_NUM * 4];
            for (int row = 0; row < ROW_NUM; ++row) {
                linePoints[row * 4] = 0;
                linePoints[row * 4 + 3] = linePoints[row * 4 + 1] = cellSize.y * (row + 1);
                linePoints[row * 4 + 2] = vWidth;
            }
            linePaint.setColor(rowSepLineColor);
            canvas.drawLines(linePoints, linePaint);
        }

        private void triggerDateTapEvent(MotionEvent e) {
            int column = (int) Math.ceil(e.getX() / cellSize.x) - 1;
            int row = (int) Math.ceil(e.getY() / cellSize.y) - 1;
            int index = row * DAYS_PER_WEEK + column;
            CalendarCell cell = internalCellList.get(index);

            if (autoScroll) {
                int state = -1;
                if ((state = outOfMonth(cell.getDateMillis())) > 0) {
                    onTapOut(cell, state);//tap out of month do not show background
                } else {
                    redrawTapArea(row, column);
                }
            } else {
                redrawTapArea(row, column);
            }
            if (dateTapListener != null) {
                dateTapListener.onTap(internalCellList.get(index));
            }
        }

        /**
         * invoke {@link #invalidate()} to draw the tap area
         *
         * @param row
         * @param column
         */
        private void redrawTapArea(int row, int column) {
            if (highlightAreas == null)
                highlightAreas = new LinkedList();
            RectF dirty = new RectF(cellSize.x * column,
                    cellSize.y * row,
                    cellSize.x * (column + 1),
                    cellSize.y * (row + 1));
            RectF popRect = null;
            if (highlightAreas.size() >= maxHighlightNum) {
                popRect = highlightAreas.pop();
            }
            highlightAreas.add(dirty);
//            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
//                highlightAreas.add(new RectF(dirty));//In API 19 and below, invalidate method may be destructive to dirty.
//            } else {
//                highlightAreas.add(dirty);
//            }
//        invalidate(dirty);
//        if (popRect != null) {
//            invalidate(popRect);
//        }
            invalidate();
        }

        private int outOfMonth(long time) {
            int ret = -1;
            if (CalendarUtil.compareDay(time, monthFirstDayMillis) < 0)
                ret = TAP_BEFORE_HEAD;
            if (CalendarUtil.compareDay(time, monthLastDayMillis) > 0)
                ret = TAP_AFTER_TAIL;
            return ret;
        }

        private GestureDetector.OnGestureListener gestureListener = new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
//                MFLog.d("gestureListener onDown");
                return true;
            }

            @Override
            public void onShowPress(MotionEvent e) {
                MFLog.d("gestureListener onShowPress");
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
//                MFLog.d("gestureListener onSingleTapUp");
                triggerDateTapEvent(e);
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//                MFLog.d("gestureListener onScroll");
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
//                MFLog.d("gestureListener onLongPress");
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//                MFLog.d("gestureListener onFling");
                return true;
            }
        };
    }
}
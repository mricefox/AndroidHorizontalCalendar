package com.mricefox.androidhorizontalcalendar.calendar;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.mricefox.androidhorizontalcalendar.assist.CalendarUtil;
import com.mricefox.androidhorizontalcalendar.assist.MFLog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/11/26
 */
class MonthView extends View {
    private final static int ROW_NUM = 0x6;
    private final static int DAYS_PER_WEEK = 0x7;
    private static final long MILLIS_IN_DAY = 86400000L;

    private List<AbstractCalendarCell> internalCellList;
    private long firstDayMillis, lastDayMillis;
    private long monthFirstDayMillis, monthLastDayMillis;
    private int rowSepLineColor;

    private Paint datePaint;
    private Paint linePaint;
    private Paint bgPaint;
    private int vWidth, vHeight;
    private Calendar paintCalendar;
    private PointF paintSize;
    private RectF paintArea;

    private GestureDetector gestureDetector;
    private HorizontalCalendarView.OnDateTapListener dateTapListener;
    private LinkedList<RectF> highlightAreas;
    private int highlightColor;
    private int maxHighlightNum;

    private static Comparator<AbstractCalendarCell> cellComparator = new Comparator<AbstractCalendarCell>() {
        @Override
        public int compare(AbstractCalendarCell lhs, AbstractCalendarCell rhs) {
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

    public MonthView(Context context) {
        super(context);
        MFLog.d(this + "monthview consturct");
        initialize();
    }

    public MonthView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    //
    public MonthView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MonthView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize();
    }

//    void setCalendarCellList(List<CalendarCell> calendarCellList) {
//        this.calendarCellList = calendarCellList;
//    }

    void initData(Calendar monthCal, List<AbstractCalendarCell> data,
                  int firstDayOfWeek, int weekendColor, int rowSepLineColor,
                  HorizontalCalendarView.OnDateTapListener listener,
                  int highlightColor, int maxHighlightNum) {
        MFLog.d(this + "monthview initData");
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
        internalCellList = getMonthData(data, weekendColor);

//        Calendar c = Calendar.getInstance();
//
//        MFLog.d("-------------");
//        for (int i = 0; i < internalCellList.size(); ++i) {
//            c.setTimeInMillis(internalCellList.get(i).getDateMillis());
//            MFLog.d("internalCellList i:" + CalendarUtil.calendar2Str(c));
//        }
//        MFLog.d("-------------");

        this.rowSepLineColor = rowSepLineColor;
        dateTapListener = listener;
        this.highlightColor = highlightColor;
        this.maxHighlightNum = maxHighlightNum;
    }

    /**
     * get specified month data from total data, including the extra week day that is not in the month
     *
     * @param
     * @return
     */
    private List<AbstractCalendarCell> getMonthData(List<AbstractCalendarCell> sourceData, int weekendColor) {
        List<AbstractCalendarCell> monthData = new ArrayList();
        for (int i = 0; i < ROW_NUM * DAYS_PER_WEEK; ++i) {
            AbstractCalendarCell cell = new CalendarCell(firstDayMillis + i * MILLIS_IN_DAY);
            int index = -1;
            if (sourceData != null &&
                    (index = Collections.binarySearch(sourceData, cell, cellComparator)) >= 0) {
                cell = sourceData.get(index);
            }
            monthData.add(cell);

            int weekDay = CalendarUtil.getWeekday(cell.getDateMillis());
            if (weekDay == Calendar.SUNDAY || weekDay == Calendar.SATURDAY) {
                cell.setDateTextNormalColor(weekendColor);
            }
        }
        return monthData;
    }

    private void initialize() {
        paintCalendar = Calendar.getInstance();
        paintSize = new PointF();
        paintArea = new RectF();
//        hightlightAreas = new LinkedList();

        bgPaint = new Paint();
        bgPaint.setAntiAlias(true);
        bgPaint.setStyle(Paint.Style.FILL);
//        bgPaint.setColor(highlightColor);

        datePaint = new Paint();
        datePaint.setAntiAlias(true);
        datePaint.setStyle(Paint.Style.STROKE);
        datePaint.setTextAlign(Paint.Align.CENTER);
//        datePaint.setStrokeWidth(10);

        linePaint = new Paint();
        linePaint.setAntiAlias(true);

        gestureDetector = new GestureDetector(getContext(), gestureListener);
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
        MFLog.d(this + "monthview onSizeChanged h:" + h + " w:" + w);
        vHeight = h;
        vWidth = w;
        paintSize.set((vWidth + 0f) / DAYS_PER_WEEK, (vHeight + 0f) / ROW_NUM);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        MFLog.d("onTouchEvent x:" + event.getX() + " y:" + event.getY());
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
                paintArea.set(paintSize.x * column, paintSize.y * row,
                        paintSize.x * (column + 1), paintSize.y * (row + 1));
                AbstractCalendarCell cell = internalCellList.get(row * DAYS_PER_WEEK + column);
                drawDateCell(canvas, cell, paintArea);
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
    private void drawDateCell(Canvas canvas, AbstractCalendarCell cell, RectF loc) {
        paintCalendar.setTimeInMillis(cell.getDateMillis());
        String dateTxt = String.valueOf((paintCalendar.get(Calendar.DAY_OF_MONTH)));
        //draw date
        if (highlightAreas != null && highlightAreas.contains(loc)) {
            datePaint.setColor(cell.getDateHighlightColor());
        } else {
            datePaint.setColor(cell.getDateTextNormalColor());
        }
        datePaint.setTextSize(cell.getDateTextSize());
        float typefaceOffset = -datePaint.ascent() / 2;//recommended height
//        MFLog.d("typefaceOffset = "+typefaceOffset);
        canvas.drawText(dateTxt, loc.centerX(), loc.centerY() + typefaceOffset, datePaint);
    }

    private void drawRowSepLines(Canvas canvas) {
        float[] linePoints = new float[ROW_NUM * 4];
        for (int row = 0; row < ROW_NUM; ++row) {
            linePoints[row * 4] = 0;
            linePoints[row * 4 + 3] = linePoints[row * 4 + 1] = paintSize.y * (row + 1);
            linePoints[row * 4 + 2] = vWidth;
        }
        linePaint.setColor(rowSepLineColor);
        canvas.drawLines(linePoints, linePaint);
    }

    private void triggerDateTapEvent(MotionEvent e) {
        int column = (int) Math.ceil(e.getX() / paintSize.x) - 1;
        int row = (int) Math.ceil(e.getY() / paintSize.y) - 1;
        int index = row * DAYS_PER_WEEK + column;

        if (dateTapListener != null) {
            dateTapListener.onTap(internalCellList.get(index));
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(internalCellList.get(index).getDateMillis());
            MFLog.d("tap date:" + CalendarUtil.calendar2Str(c));
        }
        redrawTapArea(row, column);
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
        RectF dirty = new RectF(paintSize.x * column,
                paintSize.y * row,
                paintSize.x * (column + 1),
                paintSize.y * (row + 1));
        RectF popRect = null;
        if (highlightAreas.size() >= maxHighlightNum) {
            popRect = highlightAreas.pop();
        }
        highlightAreas.add(new RectF(dirty));//In API 19 and below, invalidate method may be destructive to dirty.
//        invalidate(dirty);
//        if (popRect != null) {
//            invalidate(popRect);
//        }
        invalidate();
    }

    private GestureDetector.OnGestureListener gestureListener = new GestureDetector.OnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            MFLog.d("gestureListener onDown");
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {
            MFLog.d("gestureListener onShowPress");
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            MFLog.d("gestureListener onSingleTapUp");
            triggerDateTapEvent(e);
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            MFLog.d("gestureListener onScroll");
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            MFLog.d("gestureListener onLongPress");
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            MFLog.d("gestureListener onFling");
            return true;
        }
    };
}

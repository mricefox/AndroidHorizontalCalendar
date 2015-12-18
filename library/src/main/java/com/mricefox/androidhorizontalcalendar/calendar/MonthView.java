package com.mricefox.androidhorizontalcalendar.calendar;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import com.mricefox.androidhorizontalcalendar.assist.CalendarUtil;
import com.mricefox.androidhorizontalcalendar.assist.MFLog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
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

    private static List<CalendarCell> internalCellList;
    private int firstDayOfWeek;
    private long firstDayMillis, lastDayMillis;
    private long monthFirstDayMillis, monthLastDayMillis;

    //for paint
    private static Paint drawDatePaint;
    private int vWidth, vHeight;
    private static Calendar paintCalendar = Calendar.getInstance();
    private static PointF paintSize = new PointF();
    private static RectF paintRect = new RectF();

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
        initializePaints();
    }

    public MonthView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializePaints();
    }

    //
    public MonthView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializePaints();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MonthView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initializePaints();
    }

//    void setCalendarCellList(List<CalendarCell> calendarCellList) {
//        this.calendarCellList = calendarCellList;
//    }

    void initData(Calendar monthCal, List<CalendarCell> sourceData, int firstDayOfWeek, int weekendColor) {
        MFLog.d(this + "monthview initData");
        Calendar firstDayOfMonth = (Calendar) monthCal.clone();
        firstDayOfMonth.set(Calendar.DAY_OF_MONTH, 1);
        final int f_weekDay = firstDayOfMonth.get(Calendar.DAY_OF_WEEK);//the day of first day of the month
        final int headDayNum = CalendarUtil.getOffsetFirstDayOfWeek(firstDayOfWeek, f_weekDay);
//        MFLog.d("headDayNum:" + headDayNum);
        monthFirstDayMillis = firstDayOfMonth.getTimeInMillis();
//        MFLog.d("monthFirstDayMillis:" + monthFirstDayMillis);
        firstDayMillis = monthFirstDayMillis - headDayNum * MILLIS_IN_DAY;
        lastDayMillis = monthFirstDayMillis + (ROW_NUM * DAYS_PER_WEEK - 1) * MILLIS_IN_DAY;

        Calendar lastDayOfMonth = (Calendar) monthCal.clone();
        lastDayOfMonth.set(Calendar.DAY_OF_MONTH, lastDayOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH));
        monthLastDayMillis = lastDayOfMonth.getTimeInMillis();

        internalCellList = getMonthData(sourceData, weekendColor);
    }

    /**
     * get specified month data from total data, including the extra week day that is not in the month
     *
     * @param
     * @return
     */
    private List<CalendarCell> getMonthData(List<CalendarCell> sourceData, int weekendColor) {
        List<CalendarCell> monthData = new ArrayList();
        for (int i = 0; i < ROW_NUM * DAYS_PER_WEEK; ++i) {
            CalendarCell cell = new CalendarCell(firstDayMillis + i * MILLIS_IN_DAY);
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

    private void initializePaints() {
        drawDatePaint = new Paint();
        drawDatePaint.setAntiAlias(true);
        drawDatePaint.setStyle(Paint.Style.STROKE);
        drawDatePaint.setTextAlign(Paint.Align.CENTER);
        drawDatePaint.setTextSize(80.0f);// TODO: 2015/11/26
        drawDatePaint.setStrokeWidth(10);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        long time = System.currentTimeMillis();
        drawDays(canvas);
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

    /**
     * Draw days
     *
     * @param canvas
     */
    private void drawDays(Canvas canvas) {
        for (int row = 0; row < ROW_NUM; ++row) {
            for (int column = 0; column < DAYS_PER_WEEK; ++column) {
                paintRect.set(paintSize.x * column, paintSize.y * row,
                        paintSize.x * column + paintSize.x,
                        paintSize.y * row + paintSize.y);
                CalendarCell cell = internalCellList.get(row * DAYS_PER_WEEK + column);
                drawCell(canvas, cell, paintRect);
            }
        }
    }

    private void drawCell(Canvas canvas, CalendarCell cell, RectF loc) {
        paintCalendar.setTimeInMillis(cell.getDateMillis());
        String dateTxt = String.valueOf((paintCalendar.get(Calendar.DAY_OF_MONTH)));
        drawDatePaint.setColor(cell.getDateTextNormalColor());
        canvas.drawText(dateTxt, loc.left + loc.width() / 2, loc.top + loc.height() / 2, drawDatePaint);
        canvas.drawRect(loc, drawDatePaint);
        canvas.drawPoint(loc.centerX(), loc.centerY(), drawDatePaint);

        Rect r = new Rect();
        drawDatePaint.getTextBounds(dateTxt, 0, dateTxt.length(), r);
        MFLog.d("rect:" + r);

        MFLog.d("rect h:" + r.height());
        MFLog.d("descent:" + drawDatePaint.descent() + " ascent:" + drawDatePaint.ascent());

        MFLog.d("descent h:" + (drawDatePaint.descent()-drawDatePaint.ascent()));
//        canvas.drawLine(loc.left,);
    }
}

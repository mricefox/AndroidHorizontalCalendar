package com.mricefox.androidhorizontalcalendar.calendar;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import com.mricefox.androidhorizontalcalendar.assist.CalendarUtil;
import com.mricefox.androidhorizontalcalendar.assist.MFLog;

import java.util.ArrayList;
import java.util.Calendar;
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

    private List<CalendarCell> internalCellList;
    private int firstDayOfWeek;
    private long firstDayMillis, lastDayMillis;
    private long monthFirstDayMillis, monthLastDayMillis;

    private Paint drawDatePaint;
    private int vWidth, vHeight;

    public MonthView(Context context) {
        super(context);
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

    void initData(Calendar monthCal, List<CalendarCell> sourceData, int firstDayOfWeek) {
        Calendar firstDayOfMonth = (Calendar) monthCal.clone();
        firstDayOfMonth.set(Calendar.DAY_OF_MONTH, 1);
        final int f_weekDay = firstDayOfMonth.get(Calendar.DAY_OF_WEEK);//the day of first day of the month
        int headDayNum = CalendarUtil.getOffsetFirstDayOfWeek(firstDayOfWeek, f_weekDay);
        monthFirstDayMillis = firstDayOfMonth.getTimeInMillis();
        firstDayMillis = monthFirstDayMillis - headDayNum * MILLIS_IN_DAY;
        lastDayMillis = monthFirstDayMillis + (ROW_NUM * DAYS_PER_WEEK - 1) * MILLIS_IN_DAY;

        Calendar lastDayOfMonth = (Calendar) monthCal.clone();
        lastDayOfMonth.set(Calendar.DAY_OF_MONTH, lastDayOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH));
        monthLastDayMillis = lastDayOfMonth.getTimeInMillis();

        internalCellList = getMonthData(sourceData);
    }

//    /**
//     * get specified month data from total data, including the extra week day that is not in the month
//     *
//     * @param
//     * @return
//     */
//    private List<CalendarCell> getMonthData(List<CalendarCell> sourceData) {
//        long startTime = System.currentTimeMillis();
//        long time = 0;
//        if (sourceData != null && sourceData.get(0) != null)
//            time = sourceData.get(0).getDateMillis();
//        int offset = (int) ((firstDayMillis - time) / MILLIS_IN_DAY);
//        List<CalendarCell> target = new ArrayList();
//
//        for (int i = 0; i < ROW_NUM * DAYS_PER_WEEK; ++i) {
//            CalendarCell source = sourceData == null ? null : sourceData.get(i + offset);
//            if (source == null) {//if data source invalid
//                CalendarCell cell = new CalendarCell(firstDayMillis + i * MILLIS_IN_DAY);
//                MFLog.d("add cell color:" + cell.getDateTextNormalColor());
//                target.add(cell);
//            } else {
//                MFLog.d("add source color:" + source.getDateTextNormalColor());
//                target.add(source);
//            }
//        }
//        MFLog.d("getMonthData time:" + (System.currentTimeMillis() - startTime));
//        return target;
//    }

    /**
     * get specified month data from total data, including the extra week day that is not in the month
     *
     * @param
     * @return
     */
    private List<CalendarCell> getMonthData(List<CalendarCell> sourceData) {
        //todo Traverse sourceData twice, see use Collections.binarySearch()
        List<CalendarCell> monthData = new ArrayList();
        for (int i = 0; i < ROW_NUM * DAYS_PER_WEEK; ++i) {
            CalendarCell cell = new CalendarCell(firstDayMillis + i * MILLIS_IN_DAY);
            if (sourceData != null && sourceData.contains(cell)) {
                int index = sourceData.indexOf(cell);
                monthData.add(sourceData.get(index));
                MFLog.d("add sourceData");
            } else {
                MFLog.d("add cell");
                monthData.add(cell);
            }
        }
        return monthData;
    }


    private void initializePaints() {
        drawDatePaint = new Paint();
        drawDatePaint.setAntiAlias(true);
        drawDatePaint.setStyle(Paint.Style.FILL);
        drawDatePaint.setTextAlign(Paint.Align.CENTER);
        drawDatePaint.setColor(Color.RED);// TODO: 2015/11/26
        drawDatePaint.setTextSize(80.0f);// TODO: 2015/11/26
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawDays(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        vHeight = h;
        vWidth = w;
    }

    /**
     * Draw days
     *
     * @param canvas
     */
    private void drawDays(Canvas canvas) {
        Calendar calendar = Calendar.getInstance();
        PointF p = new PointF((vWidth + 0f) / DAYS_PER_WEEK, (vHeight + 0f) / ROW_NUM);

//        drawDatePaint.setStrokeWidth(20);
//        canvas.drawText("hh",100,100,drawDatePaint);

        for (int i = 0; i < ROW_NUM; ++i) {
            for (int j = 0; j < DAYS_PER_WEEK; ++j) {
                CalendarCell cell = internalCellList.get(i * DAYS_PER_WEEK + j);
                calendar.setTimeInMillis(cell.getDateMillis());
                String dateTxt = String.valueOf((calendar.get(Calendar.DAY_OF_MONTH)));

                drawDatePaint.setColor(cell.getDateTextNormalColor());
                canvas.drawText(dateTxt, p.x * j + p.x / 2, p.y * i + p.y / 2, drawDatePaint);
//                MFLog.d("i=" + i + " j=" + j);
            }
        }
    }

    private void drawCell(Canvas canvas) {

    }
}

package com.mricefox.androidhorizontalcalendar.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
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
    private final static int ROW_NUM = 6;
    private final static int DAYS_PER_WEEK = 7;
    private static final long MILLIS_IN_DAY = 86400000L;

    private List<CalendarCell> internalCellList;
    private int firstDayOfWeek;
    private Paint drawDatePaint;
    private int vWidth, vHeight;

    public MonthView(Context context) {
        super(context);

        initializePaints();
    }

//    public MonthView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//    }
//
//    public MonthView(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//    }
//
//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    public MonthView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//    }

//    void setCalendarCellList(List<CalendarCell> calendarCellList) {
//        this.calendarCellList = calendarCellList;
//    }

    void initData(Calendar monthCal, List<CalendarCell> calendarData, int firstDayOfWeek) {
        Calendar firstDayOfMonth = (Calendar) monthCal.clone();
        firstDayOfMonth.set(Calendar.DAY_OF_MONTH, 1);
        final int f_weekDay = firstDayOfMonth.get(Calendar.DAY_OF_WEEK);//the day of first day of the month
        int headDayNum = 0;//todo other first day of week
        if (firstDayOfWeek == Calendar.SUNDAY) {
            headDayNum = f_weekDay - Calendar.SUNDAY;
        } else if (firstDayOfWeek == Calendar.MONDAY) {
            headDayNum = f_weekDay == Calendar.SUNDAY ? 6 : f_weekDay - Calendar.MONDAY;
        }
        Calendar firstDay = Calendar.getInstance();
        Calendar lastDay = Calendar.getInstance();
        firstDay.setTimeInMillis(firstDayOfMonth.getTimeInMillis() - headDayNum * MILLIS_IN_DAY);
        lastDay.setTimeInMillis(firstDay.getTimeInMillis() + (ROW_NUM * DAYS_PER_WEEK - 1) * MILLIS_IN_DAY);

        internalCellList = new ArrayList<>();
        for (int i = 0; i < ROW_NUM * DAYS_PER_WEEK; ++i) {
            CalendarCell cell = new BaseCell(firstDay.getTimeInMillis() + i * MILLIS_IN_DAY);
            internalCellList.add(cell);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(cell.getDateMillis());
            MFLog.d(CalendarUtil.calendar2Str(calendar));
        }
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
                canvas.drawText(dateTxt, p.x * j + p.x / 2, p.y * i + p.y / 2, drawDatePaint);

                MFLog.d("i=" + i + " j=" + j);
            }
        }
    }

    private void drawCell(Canvas canvas) {

    }

    private class BaseCell extends AbstractCalendarCell {

        public BaseCell(long dateMillis) {
            super(dateMillis);
        }
    }

}

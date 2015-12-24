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
    public static int TAP_BEFORE_HEAD = 0x1;
    public static int TAP_AFTER_TAIL = 0x2;

    private List<AbstractCalendarCell> internalCellList;
    private long firstDayMillis, lastDayMillis;
    private long monthFirstDayMillis, monthLastDayMillis;
    private int rowSepLineColor;

    private Paint datePaint;
    private Paint linePaint;
    private Paint bgPaint;
    private int vWidth, vHeight;
    private Calendar paintCalendar;
    private PointF cellSize;
    private RectF cellArea;

    private GestureDetector gestureDetector;
    private HorizontalCalendarView.OnDateTapListener dateTapListener;
    private LinkedList<RectF> highlightAreas;
    private int highlightColor;
    private int maxHighlightNum;
    private OnTapOutOfMonthListener tapOutOfMonthListener;

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

    public interface OnTapOutOfMonthListener {
        void onTapOut(AbstractCalendarCell cell, int tailOrHead);
    }

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

    void initData(Calendar monthCal, List<AbstractCalendarCell> data,
                  int firstDayOfWeek, int weekendColor, int rowSepLineColor,
                  HorizontalCalendarView.OnDateTapListener dateTapListener,
                  int highlightColor, int maxHighlightNum,
                  OnTapOutOfMonthListener tapOutOfMonthListener) {
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

        this.rowSepLineColor = rowSepLineColor;
        this.dateTapListener = dateTapListener;
        this.highlightColor = highlightColor;
        this.maxHighlightNum = maxHighlightNum;
        this.tapOutOfMonthListener = tapOutOfMonthListener;

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
            long time = firstDayMillis + i * MILLIS_IN_DAY;
            AbstractCalendarCell cell = new CalendarCell(time);
            if (outOfMonth(time) > 0) {
                //date before the first day of this month or after the last day of month, set unavailable
                cell.setAvailableMode(0, 1);
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(time);
            }
            int index = -1;
            /**
             * if source contain this day, use source data, ignore the available above
             */
            int auto = cell.getAutoAvailable();
            if (sourceData != null &&
                    (index = Collections.binarySearch(sourceData, cell, cellComparator)) >= 0) {
                /**
                 *copy date source
                 */
                // TODO: 2015/12/24
                cell = (AbstractCalendarCell) sourceData.get(index).clone();
            }
            int manual = cell.getManualAvailable();
            cell.setAvailableMode(auto, manual);
            monthData.add(cell);

            //setup weekend color
            int weekDay = CalendarUtil.getWeekday(cell.getDateMillis());
            if (weekDay == Calendar.SUNDAY || weekDay == Calendar.SATURDAY) {
                cell.setDateTextNormalColor(weekendColor);
            }
        }
        return monthData;
    }

    private void initialize() {
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
                AbstractCalendarCell cell = internalCellList.get(row * DAYS_PER_WEEK + column);
                drawDateCell(canvas, cell, cellArea);
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
        if (cell.getAutoAvailable() == 0) {
            datePaint.setColor(Color.GRAY);
        }
        if (cell.getManualAvailable() == 0) {
            datePaint.setColor(Color.GREEN);
        }
        datePaint.setTextSize(cell.getDateTextSize());
        float typefaceOffset = -datePaint.ascent() / 2;//recommended height
        canvas.drawText(dateTxt, loc.centerX(), loc.centerY() + typefaceOffset, datePaint);
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
        AbstractCalendarCell cell = internalCellList.get(index);

        int state = -1;
        if ((state = outOfMonth(cell.getDateMillis())) > 0) {
            if (tapOutOfMonthListener != null) {
                tapOutOfMonthListener.onTapOut(cell, state);
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
        highlightAreas.add(new RectF(dirty));//In API 19 and below, invalidate method may be destructive to dirty.
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

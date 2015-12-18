package com.mricefox.androidhorizontalcalendar;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;

import com.mricefox.androidhorizontalcalendar.assist.MFLog;
import com.mricefox.androidhorizontalcalendar.calendar.CalendarCell;
import com.mricefox.androidhorizontalcalendar.calendar.CalendarViewAdapter;
import com.mricefox.androidhorizontalcalendar.calendar.HorizontalCalendarView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private HorizontalCalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        initCalendarView();
//        CalendarView
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initCalendarView() {
        calendarView = (HorizontalCalendarView) findViewById(R.id.calendar_view);
        calendarView.setAdapter(calendarViewAdapter);
    }

    private CalendarViewAdapter calendarViewAdapter = new CalendarViewAdapter() {
//        @Override
//        public long getMinDateMillis() {
//            return CalendarUtil.convertDateStr2Millis("2015-05-10");
//        }
//
//        @Override
//        public long getMaxDateMillis() {
//            return CalendarUtil.convertDateStr2Millis("2015-12-21");
//        }

        @Override
        public List<CalendarCell> getDataSource() {
            List<CalendarCell> cells = new ArrayList<>();
            for (long i = 0; i < 30; ++i) {//dummy data
                CalendarCell cell = new CalendarCell(getMinDateMillis() + 86400000L * i);
//                MFLog.d("cell m:" + (getMinDateMillis() + 86400000L * i));
                cell.setDateTextNormalColor(Color.BLUE);
                cells.add(cell);
            }
            return cells;
        }
    };
}

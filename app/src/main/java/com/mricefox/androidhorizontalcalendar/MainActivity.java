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

import com.mricefox.androidhorizontalcalendar.assist.CalendarUtil;
import com.mricefox.androidhorizontalcalendar.calendar.AbsCalendarViewAdapter;
import com.mricefox.androidhorizontalcalendar.calendar.CalendarCell;
import com.mricefox.androidhorizontalcalendar.calendar.HorizontalCalendarView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private HorizontalCalendarView calendarView;
    private List<CalendarCell> cells;

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
        initCells();
        calendarView.setAdapter(calendarViewAdapter);
        calendarView.setOnDateTapListener(new HorizontalCalendarView.OnDateTapListener() {
            @Override
            public void onTap(CalendarCell cell) {

            }
        });

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                calendarView.scrollToDate(2015, 11, 24);
//            }
//        }, 5000);
    }

    private void initCells() {
        cells = new ArrayList<>();
        long start = CalendarUtil.convertDateStr2Millis("1900-01-01");

        for (long i = 0; i < 37; ++i) {//dummy data
            CalendarCell cell = new CalendarCell(start + 86400000L * i);
            cell.setDateTextNormalColor(Color.BLUE);
            if (i == 4 || i == 7 || i == 35) {
                cell.setAvailableMode(1, 0);
            }
            cells.add(cell);
        }
    }

    private AbsCalendarViewAdapter calendarViewAdapter = new AbsCalendarViewAdapter() {
        @Override
        public List<CalendarCell> getDataSource() {
            return cells;
        }
    };
}

package com.example.jonathon.agilebudgeting;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static android.content.Intent.ACTION_MAIN;
import static android.content.Intent.ACTION_VIEW;

public class MainActivity extends AppCompatActivity implements DatePicker.OnDateChangedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DatePicker picker = (DatePicker) findViewById(R.id.planDatePicker);
        GregorianCalendar today = new GregorianCalendar();
        picker.init(today.get(Calendar.YEAR),today.get(Calendar.MONTH),today.get(Calendar.DAY_OF_MONTH),this);
    }

    public void createPlan(View view) {
        Intent intent = new Intent(this, PlanActivity.class);
        intent.setAction(ACTION_VIEW);
        intent.putExtra("com.example.jonathon.agilebudgeting.PLAN_ID", 16L);
        startActivity(intent);
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        GregorianCalendar planDate = new GregorianCalendar();
        planDate.set(year, monthOfYear, dayOfMonth);

        Intent intent = new Intent(this, PlanActivity.class);
        intent.setAction(ACTION_MAIN);
        intent.putExtra("com.example.jonathon.agilebudgeting.PLAN_DATE", planDate);
        startActivity(intent);

    }
}

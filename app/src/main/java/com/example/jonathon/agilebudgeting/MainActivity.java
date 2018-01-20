package com.example.jonathon.agilebudgeting;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.Switch;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static android.content.Intent.ACTION_MAIN;

public class MainActivity extends AppCompatActivity implements DatePicker.OnDateChangedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DbHelperSingleton.getInstance().init(getApplicationContext());

        setContentView(R.layout.activity_main);
        DatePicker picker = (DatePicker) findViewById(R.id.planDatePicker);
        GregorianCalendar today = new GregorianCalendar();
        picker.init(today.get(Calendar.YEAR),today.get(Calendar.MONTH),today.get(Calendar.DAY_OF_MONTH),this);
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        GregorianCalendar planDate = new GregorianCalendar();
        planDate.set(year, monthOfYear, dayOfMonth);
        PlanningPeriod period = new PlanningPeriod(planDate);

        Boolean useCloudData = false;
        Switch cloudDataSwitch = (Switch) findViewById(R.id.cloudStorageSwitch);
        useCloudData = cloudDataSwitch.isChecked();

        Intent intent = new Intent(this, PlanActivity.class);
        intent.setAction(ACTION_MAIN);
        intent.putExtra("com.example.jonathon.agilebudgeting.PLAN_PERIOD", period);
        intent.putExtra("com.example.jonathon.agilebudgeting.CLOUD_DATA", useCloudData);
        startActivity(intent);

    }

    protected void onDestroy() {
        DbHelperSingleton.getInstance().init(null);
        super.onDestroy();
    }
}

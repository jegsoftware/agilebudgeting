package com.example.jonathon.agilebudgeting;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Jonathon on 1/7/2017.
 */

class PlanningPeriod {
    private int periodNumber;
    private int periodYear;

    PlanningPeriod () {
        periodNumber = 0;
        periodYear = 0;
    }

    PlanningPeriod (int perNum, int perYear) {
        periodNumber = perNum;
        periodYear = perYear;
    }

    PlanningPeriod(Calendar date) {
        periodNumber = 0;
        periodYear = 0;
        setDate(date);
    }

    int getPeriodNumber() {
        return periodNumber;
    }

    int getPeriodYear() {
        return periodYear;
    }

    private void setDate(Calendar date)
    {
        int dayOfMonth = date.get(Calendar.DAY_OF_MONTH);
        int month = date.get(Calendar.MONTH);
        periodNumber = month * 2 + 1;
        if (dayOfMonth >= 15) {
            periodNumber++;
        }
        periodYear = date.get(Calendar.YEAR);
    }

    void setDate(String date) throws java.text.ParseException
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat();
        dateFormat.applyPattern("MM/dd/yyyy");
        Date dateObj = dateFormat.parse(date);
        GregorianCalendar periodDate = new GregorianCalendar();
        periodDate.setTime(dateObj);
        setDate(periodDate);
    }


    String getPeriodStartDate() {
        int month = periodNumber / 2 - 1;
        int day = 15;
        if ((periodNumber % 2) != 0) {
            month++;
            day = 1;
        }
        GregorianCalendar startDate = new GregorianCalendar(periodYear, month, day);

        SimpleDateFormat curDateFormat = new SimpleDateFormat();
        curDateFormat.applyPattern("MM/dd/yyyy");
        return curDateFormat.format(startDate.getTime());
    }


}

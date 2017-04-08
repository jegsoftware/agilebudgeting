package com.example.jonathon.agilebudgeting;

import org.junit.Before;
import org.junit.Test;

import java.util.GregorianCalendar;

import static org.junit.Assert.*;

/**
 * Created by Jonathon on 4/8/2017.
 */
public class PlanningPeriodTest {

    private final int testPeriodNum = 1;
    private final int testPeriodYear = 2017;

    private PlanningPeriod testPeriod;

    @Before
    public void setup() {
        testPeriod = new PlanningPeriod(testPeriodNum,testPeriodYear);
    }

    @Test
    public void getPeriodNumber() throws Exception {
        assertEquals(testPeriodNum, testPeriod.getPeriodNumber());
    }

    @Test
    public void getPeriodYear() throws Exception {
        assertEquals(testPeriodYear, testPeriod.getPeriodYear());
    }

    @Test
    public void setDateFromString() throws Exception {
        testPeriod.setDate("02/20/2017");
        assertEquals(4, testPeriod.getPeriodNumber());
        assertEquals(2017, testPeriod.getPeriodYear());
    }

    @Test
    public void getPeriodStartDate() throws Exception {
        testPeriod.setDate("02/20/2017");
        assertEquals("02/15/2017", testPeriod.getPeriodStartDate());
    }

}
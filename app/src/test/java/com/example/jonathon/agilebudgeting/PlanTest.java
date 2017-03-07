package com.example.jonathon.agilebudgeting;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.GregorianCalendar;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Jonathon on 2/25/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class PlanTest {

    @Mock private AgileBudgetingDbHelper dbHelperMock;
    @Mock private SQLiteDatabase dbMock;
    @Mock private Cursor planTableCursorMock;
    @Mock private Cursor itemTableCursorMock;

    private DbHelperSingleton singleton;
    private final long planId = 1L;
    private final int periodNum = 3;
    private final int periodYear = 2017;
    private final String planningStatus = "OPEN";
    private final String actualsStatus = "OPEN";

    @Before
    public void setup() {

        MockitoAnnotations.initMocks(this);
        singleton = DbHelperSingleton.getInstance();
        when(dbHelperMock.getReadableDatabase()).thenReturn(dbMock);
        when(dbHelperMock.getWritableDatabase()).thenReturn(dbMock);

        Method method;
        String methodName = "setDbHelper";

        try {
            method = DbHelperSingleton.class.getDeclaredMethod(methodName,AgileBudgetingDbHelper.class);
            method.setAccessible(true);
            method.invoke(singleton,dbHelperMock);
            method.setAccessible(false);
        }
        catch (NoSuchMethodException e)  {
            fail(e.toString());
        }
        catch (InvocationTargetException e) {
            fail(e.toString());
        }
        catch (IllegalAccessException e) {
            fail(e.toString());
        }
        setUpMockCursors();
        setUpMockQuery();
    }

    private void setUpMockCursors() {
        when(planTableCursorMock.getColumnIndex(AgileBudgetingContract.Plans._ID)).thenReturn(0);
        when(planTableCursorMock.getColumnIndex(AgileBudgetingContract.Plans.COLUMN_NAME_PLANNING_STATUS)).thenReturn(1);
        when(planTableCursorMock.getColumnIndex(AgileBudgetingContract.Plans.COLUMN_NAME_ACTUALS_STATUS)).thenReturn(2);
        when(planTableCursorMock.getColumnIndex(AgileBudgetingContract.Plans.COLUMN_NAME_PERIODNUM)).thenReturn(3);
        when(planTableCursorMock.getColumnIndex(AgileBudgetingContract.Plans.COLUMN_NAME_PERIODYEAR)).thenReturn(4);

        when(planTableCursorMock.moveToFirst()).thenReturn(true);
        when(planTableCursorMock.getLong(0)).thenReturn(planId);
        when(planTableCursorMock.getString(1)).thenReturn(planningStatus);
        when(planTableCursorMock.getString(2)).thenReturn(actualsStatus);
        when(planTableCursorMock.getInt(3)).thenReturn(periodNum);
        when(planTableCursorMock.getInt(4)).thenReturn(periodYear);

        when(itemTableCursorMock.moveToFirst()).thenReturn(true);
        when(itemTableCursorMock.isAfterLast()).thenReturn(true);
    }

    private void setUpMockQuery() {
        when(dbMock.query(
                eq(AgileBudgetingContract.Plans.TABLE_NAME),
                any(String[].class),
                anyString(),
                any(String[].class),
                anyString(),
                anyString(),
                anyString()
        )).thenReturn(planTableCursorMock);

        when(dbMock.query(
                eq(AgileBudgetingContract.Items.TABLE_NAME),
                any(String[].class),
                anyString(),
                any(String[].class),
                anyString(),
                anyString(),
                anyString()
        )).thenReturn(itemTableCursorMock);
    }

    @Test
    public void createPlanFromDate() throws Exception {
        Plan testPlan = Plan.createPlan(new GregorianCalendar(2017,2,7), new DBPlanPersister());
        assertEquals(1L, testPlan.getPlanId());
    }

    @Test
    public void createPlanFromId() throws Exception {

    }

    @Test
    public void getPlanId() throws Exception {
        Plan testPlan = Plan.createPlan(new GregorianCalendar(2017,2,7), new DBPlanPersister());
        assertEquals(1L, testPlan.getPlanId());
    }

    @Test
    public void getPlanStartDate() throws Exception {
        Plan testPlan = Plan.createPlan(new GregorianCalendar(2017,2,7), new DBPlanPersister());
        assertEquals("02/01/2017", testPlan.getPlanStartDate());
    }

    @Test
    public void setPeriod() throws Exception {

    }

    @Test
    public void setPeriod1() throws Exception {

    }

    @Test
    public void setPeriod2() throws Exception {

    }

    @Test
    public void getPeriod() throws Exception {

    }

    @Test
    public void openPlanning() throws Exception {

    }

    @Test
    public void closePlanning() throws Exception {

    }

    @Test
    public void openActuals() throws Exception {

    }

    @Test
    public void closeActuals() throws Exception {

    }

    @Test
    public void addPlannedItem() throws Exception {

    }

    @Test
    public void addDeposit() throws Exception {

    }

    @Test
    public void addActualItem() throws Exception {

    }

    @Test
    public void getTotalPlannedExpenses() throws Exception {

    }

    @Test
    public void getTotalDeposits() throws Exception {

    }

    @Test
    public void getTotalActualExpenses() throws Exception {

    }

    @Test
    public void getNetPlannedAmount() throws Exception {

    }

    @Test
    public void getNetActualAmount() throws Exception {

    }

    @Test
    public void getDeposits() throws Exception {

    }

    @Test
    public void getPlannedItems() throws Exception {

    }

    @Test
    public void isPlanningClosed() throws Exception {

    }

    @Test
    public void isPlanningOpen() throws Exception {

    }

    @Test
    public void isActualsClosed() throws Exception {

    }

    @Test
    public void isActualsOpen() throws Exception {

    }

    @Test
    public void getActualItems() throws Exception {

    }

    @Test
    public void getPlannedItemsForAccount() throws Exception {

    }

}
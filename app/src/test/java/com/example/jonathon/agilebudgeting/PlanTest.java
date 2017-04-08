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
import java.util.ArrayList;
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

    private final int periodNum = 3;
    private final int periodYear = 2017;
    private final String planningStatus = "OPEN";
    private final String actualsStatus = "OPEN";

    private TestPlanPersister planPersister;

    @Before
    public void setup() {
        planPersister = new TestPlanPersister();
    }

    @Test
    public void createPlanFromDate() throws Exception {
        Plan testPlan = Plan.createPlan(new GregorianCalendar(2017,1,7), planPersister);
        assertEquals(periodNum, testPlan.getPeriod().getPeriodNumber());
        assertEquals(periodYear, testPlan.getPeriod().getPeriodYear());
    }

    @Test
    public void createPlanFromPeriod() throws Exception {
        PlanningPeriod testPeriod = new PlanningPeriod(periodNum, periodYear);
        Plan testPlan = Plan.createPlan(testPeriod, planPersister);
        assertEquals(periodNum, testPlan.getPeriod().getPeriodNumber());
        assertEquals(periodYear, testPlan.getPeriod().getPeriodYear());
    }

    @Test
    public void getPlanStartDate() throws Exception {
        Plan testPlan = Plan.createPlan(new GregorianCalendar(2017,1,7), planPersister);
        assertEquals("02/01/2017", testPlan.getPlanStartDate());
    }

    @Test
    public void setPeriod() throws Exception {
        Plan testPlan = Plan.createPlan(new GregorianCalendar(2017,0,7), planPersister);
        assertEquals("01/01/2017", testPlan.getPlanStartDate());
        PlanningPeriod testPeriod = new PlanningPeriod(periodNum, periodYear);
        testPlan.setPeriod(testPeriod);
        assertEquals("02/01/2017", testPlan.getPlanStartDate());
    }

    @Test
    public void setPeriodByCalendar() throws Exception {
        Plan testPlan = Plan.createPlan(new GregorianCalendar(2017,0,7), planPersister);
        assertEquals("01/01/2017", testPlan.getPlanStartDate());
        testPlan.setPeriod(new GregorianCalendar(2017,1,7));
        assertEquals("02/01/2017", testPlan.getPlanStartDate());
    }

    @Test
    public void setPeriodByString() throws Exception {
        Plan testPlan = Plan.createPlan(new GregorianCalendar(2017,0,5), planPersister);
        assertEquals("01/01/2017", testPlan.getPlanStartDate());
        testPlan.setPeriod("02/07/2017");
        assertEquals("02/01/2017", testPlan.getPlanStartDate());
    }

    @Test
    public void getPeriod() throws Exception {
        Plan testPlan = Plan.createPlan(new GregorianCalendar(2017,1,7), planPersister);
        PlanningPeriod period = testPlan.getPeriod();
        assertEquals(periodNum, testPlan.getPeriod().getPeriodNumber());
        assertEquals(periodYear, testPlan.getPeriod().getPeriodYear());
    }

    @Test
    public void closeAndOpenPlanning() throws Exception {
        Plan testPlan = Plan.createPlan(new GregorianCalendar(2017,1,7), planPersister);
        assert(testPlan.isPlanningOpen());
        assertFalse(testPlan.isPlanningClosed());
        testPlan.closePlanning();
        assert(testPlan.isPlanningClosed());
        assertFalse(testPlan.isPlanningOpen());
        testPlan.openPlanning();
        assert(testPlan.isPlanningOpen());
        assertFalse(testPlan.isPlanningClosed());
    }

    @Test
    public void closeandOpenActuals() throws Exception {
        Plan testPlan = Plan.createPlan(new GregorianCalendar(2017,1,7), planPersister);
        assert(testPlan.isActualsOpen());
        assertFalse(testPlan.isActualsClosed());
        testPlan.closeActuals();
        assert(testPlan.isActualsClosed());
        assertFalse(testPlan.isActualsOpen());
        testPlan.openActuals();
        assert(testPlan.isActualsOpen());
        assertFalse(testPlan.isActualsClosed());
    }

    @Test
    public void addAndTotalPlannedItems() throws Exception {
        Plan testPlan = Plan.createPlan(new GregorianCalendar(2017,1,7), planPersister);
        PlannedItem item1 = PlannedItem.createItem(testPlan.getPeriod(), "test planned item 1", 42.00, "Checking", new TestItemPersister());
        testPlan.addPlannedItem(item1);
        PlannedItem item2 = PlannedItem.createItem(testPlan.getPeriod(), "test planned item 2", 28.00, "Checking", new TestItemPersister());
        testPlan.addPlannedItem(item2);
        PlannedItem item3 = PlannedItem.createItem(testPlan.getPeriod(), "test planned item 3", 30.00, "Checking", new TestItemPersister());
        testPlan.addPlannedItem(item3);
        assertEquals(100.00, testPlan.getTotalPlannedExpenses(), 0.00);
    }

    @Test
    public void addAndTotalDeposits() throws Exception {
        Plan testPlan = Plan.createPlan(new GregorianCalendar(2017,1,7), planPersister);
        Deposit item1 = Deposit.createDeposit(testPlan.getPeriod(), "02/08/2017", "test deposit 1", 42.00, "Checking", new TestItemPersister());
        testPlan.addDeposit(item1);
        Deposit item2 = Deposit.createDeposit(testPlan.getPeriod(), "02/09/2017", "test deposit 2", 28.00, "Checking", new TestItemPersister());
        testPlan.addDeposit(item2);
        Deposit item3 = Deposit.createDeposit(testPlan.getPeriod(), "02/10/2017", "test deposit 3", 30.00, "Checking", new TestItemPersister());
        testPlan.addDeposit(item3);
        assertEquals(100.00, testPlan.getTotalDeposits(), 0.00);
    }

    @Test
    public void addAndTotalActualItems() throws Exception {
        Plan testPlan = Plan.createPlan(new GregorianCalendar(2017,1,7), planPersister);
        ActualItem item1 = ActualItem.createActualItem(testPlan.getPeriod(), "02/08/2017", "test Actual Item 1", 42.00, "Checking", new TestItemPersister());
        testPlan.addActualItem(item1);
        ActualItem item2 = ActualItem.createActualItem(testPlan.getPeriod(), "02/09/2017", "test Actual Item 2", 28.00, "Checking", new TestItemPersister());
        testPlan.addActualItem(item2);
        ActualItem item3 = ActualItem.createActualItem(testPlan.getPeriod(), "02/10/2017", "test Actual Item 3", 30.00, "Checking", new TestItemPersister());
        testPlan.addActualItem(item3);
        assertEquals(100.00, testPlan.getTotalActualExpenses(), 0.00);
    }

    @Test
    public void getNetPlannedAmount() throws Exception {
        Plan testPlan = Plan.createPlan(new GregorianCalendar(2017,1,7), planPersister);
        PlannedItem item1 = PlannedItem.createItem(testPlan.getPeriod(), "test planned item 1", 42.00, "Checking", new TestItemPersister());
        testPlan.addPlannedItem(item1);
        PlannedItem item2 = PlannedItem.createItem(testPlan.getPeriod(), "test planned item 2", 28.00, "Checking", new TestItemPersister());
        testPlan.addPlannedItem(item2);
        PlannedItem item3 = PlannedItem.createItem(testPlan.getPeriod(), "test planned item 3", 30.00, "Checking", new TestItemPersister());
        testPlan.addPlannedItem(item3);
        Deposit dep1 = Deposit.createDeposit(testPlan.getPeriod(), "02/08/2017", "test deposit 1", 42.00, "Checking", new TestItemPersister());
        testPlan.addDeposit(dep1);
        Deposit dep2 = Deposit.createDeposit(testPlan.getPeriod(), "02/09/2017", "test deposit 2", 68.00, "Checking", new TestItemPersister());
        testPlan.addDeposit(dep2);
        assertEquals(10.00,testPlan.getNetPlannedAmount(),0.00);
    }

    @Test
    public void getNetActualAmount() throws Exception {
        Plan testPlan = Plan.createPlan(new GregorianCalendar(2017,1,7), planPersister);
        PlannedItem item1 = PlannedItem.createItem(testPlan.getPeriod(), "test planned item 1", 42.00, "Checking", new TestItemPersister());
        testPlan.addPlannedItem(item1);
        PlannedItem item2 = PlannedItem.createItem(testPlan.getPeriod(), "test planned item 2", 28.00, "Checking", new TestItemPersister());
        testPlan.addPlannedItem(item2);
        PlannedItem item3 = PlannedItem.createItem(testPlan.getPeriod(), "test planned item 3", 30.00, "Checking", new TestItemPersister());
        testPlan.addPlannedItem(item3);
        ActualItem actualItem1 = ActualItem.createActualItem(testPlan.getPeriod(), "02/08/2017", "test Actual Item 1", 42.00, "Checking", new TestItemPersister());
        testPlan.addActualItem(actualItem1);
        ActualItem actualItem2 = ActualItem.createActualItem(testPlan.getPeriod(), "02/09/2017", "test Actual Item 2", 25.00, "Checking", new TestItemPersister());
        testPlan.addActualItem(actualItem2);
        ActualItem actualItem3 = ActualItem.createActualItem(testPlan.getPeriod(), "02/10/2017", "test Actual Item 3", 32.00, "Checking", new TestItemPersister());
        testPlan.addActualItem(actualItem3);
        assertEquals(1.00,testPlan.getNetActualAmount(),0.00);
    }

    @Test
    public void getPlannedItemsForAccount() throws Exception {
        Plan testPlan = Plan.createPlan(new GregorianCalendar(2017,1,7), planPersister);
        PlannedItem item1 = PlannedItem.createItem(testPlan.getPeriod(), "test planned item 1", 42.00, "Checking", new TestItemPersister());
        testPlan.addPlannedItem(item1);
        PlannedItem item2 = PlannedItem.createItem(testPlan.getPeriod(), "test planned item 2", 28.00, "Savings", new TestItemPersister());
        testPlan.addPlannedItem(item2);
        PlannedItem item3 = PlannedItem.createItem(testPlan.getPeriod(), "test planned item 3", 30.00, "Checking", new TestItemPersister());
        testPlan.addPlannedItem(item3);
        ArrayList<PlannedItem> checkingItems = testPlan.getPlannedItemsForAccount("Checking");
        assertEquals(2, checkingItems.size());
        assertEquals(1, testPlan.getPlannedItemsForAccount("Savings").size());
    }

}
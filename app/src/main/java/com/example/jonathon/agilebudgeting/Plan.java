package com.example.jonathon.agilebudgeting;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

/**
 * Created by Jonathon on 1/7/2017.
 */

public class Plan {
    private double amountToCapitalOne;
    private double amountToSavings;
    private PlanStatus planningStatus;
    private PlanStatus actualsStatus;
    private PlanningPeriod period;
    private ArrayList<PlannedItem> plannedItems;
    private ArrayList<Deposit> deposits;
    private long planId;
    private ArrayList<ActualItem> actualItems;

    private Plan()
    {
        amountToCapitalOne = 0;
        amountToSavings = 0;
        planningStatus = PlanStatus.OPEN;
        actualsStatus = PlanStatus.OPEN;
        plannedItems = new ArrayList<PlannedItem>();
        deposits = new ArrayList<Deposit>();
        actualItems = new ArrayList<ActualItem>();
        planId = -1;
    }

    public static Plan createPlan(PlanningPeriod planPeriod, IPersistPlan persister) {
        Plan newPlan = new Plan();
        return persister.populate(newPlan, planPeriod);
    }

    public static Plan createPlan(Calendar planDate) {
        PlanningPeriod planPeriod = new PlanningPeriod(planDate);

        return createPlan(planPeriod);
    }

    public static Plan createPlan(PlanningPeriod planPeriod)
    {
        Plan newPlan = new Plan();
        AgileBudgetingDbHelper dbHelper = DbHelperSingleton.getInstance().getDbHelper();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                AgileBudgetingContract.Plans._ID,
                AgileBudgetingContract.Plans.COLUMN_NAME_PLANNING_STATUS,
                AgileBudgetingContract.Plans.COLUMN_NAME_ACTUALS_STATUS,
                AgileBudgetingContract.Plans.COLUMN_NAME_PERIODYEAR,
                AgileBudgetingContract.Plans.COLUMN_NAME_PERIODNUM,
        };

        String selection =
                AgileBudgetingContract.Plans.COLUMN_NAME_PERIODNUM + " = ? AND " +
                AgileBudgetingContract.Plans.COLUMN_NAME_PERIODYEAR + " = ?";
        String[] selectionArgs = {
                String.valueOf(planPeriod.getPeriodNumber()),
                String.valueOf(planPeriod.getPeriodYear())
        };

        Cursor cursor = db.query(
                AgileBudgetingContract.Plans.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        cursor.moveToFirst();

        if (cursor.isAfterLast()) {
            newPlan.setPeriod(planPeriod);
        }
        else {
            long planId = cursor.getLong(cursor.getColumnIndex(AgileBudgetingContract.Plans._ID));
            String planningStatusString = cursor.getString(cursor.getColumnIndex(AgileBudgetingContract.Plans.COLUMN_NAME_PLANNING_STATUS));
            String actualsStatusString = cursor.getString(cursor.getColumnIndex(AgileBudgetingContract.Plans.COLUMN_NAME_ACTUALS_STATUS));
            int perNum = cursor.getInt(cursor.getColumnIndex(AgileBudgetingContract.Plans.COLUMN_NAME_PERIODNUM));
            int perYear = cursor.getInt(cursor.getColumnIndex(AgileBudgetingContract.Plans.COLUMN_NAME_PERIODYEAR));

            newPlan.planId = planId;
            newPlan.planningStatus = PlanStatus.valueOf(planningStatusString);
            newPlan.actualsStatus = PlanStatus.valueOf(actualsStatusString);
            newPlan.period = new PlanningPeriod(perNum, perYear);
            newPlan.populateItems();
        }
        cursor.close();
        db.close();

        return newPlan;

    }

    public static Plan createPlan(long planId)
    {
        Plan newPlan = new Plan();
        newPlan.planId = planId;

        AgileBudgetingDbHelper dbHelper = DbHelperSingleton.getInstance().getDbHelper();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                AgileBudgetingContract.Plans.COLUMN_NAME_PLANNING_STATUS,
                AgileBudgetingContract.Plans.COLUMN_NAME_ACTUALS_STATUS,
                AgileBudgetingContract.Plans.COLUMN_NAME_PERIODYEAR,
                AgileBudgetingContract.Plans.COLUMN_NAME_PERIODNUM,
        };

        String selection = AgileBudgetingContract.Plans._ID + " = ?";
        String[] selectionArgs = { String.valueOf(planId) };

        Cursor cursor = db.query(
                AgileBudgetingContract.Plans.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        cursor.moveToFirst();
        String planningStatusString = cursor.getString(cursor.getColumnIndex(AgileBudgetingContract.Plans.COLUMN_NAME_PLANNING_STATUS));
        String actualsStatusString = cursor.getString(cursor.getColumnIndex(AgileBudgetingContract.Plans.COLUMN_NAME_ACTUALS_STATUS));
        int perNum = cursor.getInt(cursor.getColumnIndex(AgileBudgetingContract.Plans.COLUMN_NAME_PERIODNUM));
        int perYear = cursor.getInt(cursor.getColumnIndex(AgileBudgetingContract.Plans.COLUMN_NAME_PERIODYEAR));

        cursor.close();
        db.close();

        newPlan.planningStatus = PlanStatus.valueOf(planningStatusString);
        newPlan.actualsStatus = PlanStatus.valueOf(actualsStatusString);
        newPlan.period = new PlanningPeriod(perNum, perYear);
        newPlan.populateItems();
        return newPlan;
    }

    public long getPlanId() {
        return planId;
    }

    public void setPlanId(long planId) {
        this.planId = planId;
    }

    public String getPlanStartDate() {
        return period.getPeriodStartDate();
    }


    public void setPeriod (PlanningPeriod per) {
        period = per;
        persist();
    }

    public void setPeriod (Calendar planDate) {
        period = new PlanningPeriod(planDate);
        persist();
    }

    public void setPeriod (String planDate) throws ParseException {
        period = new PlanningPeriod();
        period.setDate(planDate);
        persist();
    }

    public PlanningPeriod getPeriod () {
        return period;
    }

    public void openPlanning() {
        planningStatus = PlanStatus.OPEN;
        persist();
    }

    public void closePlanning() {
        planningStatus = PlanStatus.CLOSED;
        persist();
    }

    public void openActuals() {
        actualsStatus = PlanStatus.OPEN;
        persist();
    }

    public void closeActuals() {
        actualsStatus = PlanStatus.CLOSED;
        persist();
    }

    public void addPlannedItem(PlannedItem item) {
        plannedItems.add(item);
        persist();
    }

    public void addDeposit(Deposit dep) {
        deposits.add(dep);
        persist();
    }

    public void addActualItem(ActualItem actualItem) {
        actualItems.add(actualItem);
        persist();
    }

    public double getTotalPlannedExpenses() {
        double totalExpenses = 0.00;
        for (PlannedItem item: plannedItems) {
            totalExpenses += item.getAmount();
        }
        return totalExpenses;
    }

    public double getTotalDeposits() {
        double totalDeposits = 0.00;
        for (Deposit dep: deposits) {
            totalDeposits += dep.getAmount();
        }
        return totalDeposits;
    }

    public double getTotalActualExpenses() {
        double totalExpenses = 0.00;
        for (ActualItem item: actualItems) {
            totalExpenses += item.getAmount();
        }
        return totalExpenses;
    }


    public double getNetPlannedAmount() {
        return getTotalDeposits() - getTotalPlannedExpenses();
    }

    public double getNetActualAmount() { return getTotalPlannedExpenses() - getTotalActualExpenses(); }

    private void populateItems() {
        AgileBudgetingDbHelper dbHelper = DbHelperSingleton.getInstance().getDbHelper();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                AgileBudgetingContract.Items._ID,
                AgileBudgetingContract.Items.COLUMN_NAME_TYPE,
        };

        String selection = AgileBudgetingContract.Items.COLUMN_NAME_PLANID + " = ?";
        String[] selectionArgs = {
                String.valueOf(planId),
        };

        Cursor cursor = db.query(
                AgileBudgetingContract.Items.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String type = cursor.getString(cursor.getColumnIndex(AgileBudgetingContract.Items.COLUMN_NAME_TYPE));
            long itemId = cursor.getLong(cursor.getColumnIndex(AgileBudgetingContract.Items._ID));

            if ("Deposit".equals(type)) {
                Deposit deposit = Deposit.createDeposit(itemId);
                addDeposit(deposit);
            }
            else if ("PlannedItem".equals(type)) {
                PlannedItem plannedItem = PlannedItem.createItem(itemId);
                addPlannedItem(plannedItem);
            }
            else if ("ActualItem".equals(type)) {
                ActualItem actualItem = ActualItem.createActualItem(itemId);
                addActualItem(actualItem);
            }

            cursor.moveToNext();
        }

        cursor.close();
        db.close();
    }

    public long persist() {
        AgileBudgetingDbHelper dbHelper = DbHelperSingleton.getInstance().getDbHelper();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AgileBudgetingContract.Plans.COLUMN_NAME_PERIODNUM, period.getPeriodNumber());
        values.put(AgileBudgetingContract.Plans.COLUMN_NAME_PERIODYEAR, period.getPeriodYear());
        values.put(AgileBudgetingContract.Plans.COLUMN_NAME_PLANNING_STATUS, planningStatus.name());
        values.put(AgileBudgetingContract.Plans.COLUMN_NAME_ACTUALS_STATUS, actualsStatus.name());

        if (planId == -1) {
            planId = db.insert(AgileBudgetingContract.Plans.TABLE_NAME, null, values);
        }
        else {
            String selection = AgileBudgetingContract.Plans._ID + " = ?";
            String[] selectionArgs = { String.valueOf(planId) };

            db.update(AgileBudgetingContract.Plans.TABLE_NAME, values, selection, selectionArgs);
        }
        db.close();

        return planId;
    }

    public ArrayList<Deposit> getDeposits() {
        return deposits;
    }

    public ArrayList<PlannedItem> getPlannedItems() {
        return plannedItems;
    }

    public boolean isPlanningClosed() {
        return planningStatus == PlanStatus.CLOSED;
    }

    public boolean isPlanningOpen() {
        return planningStatus == PlanStatus.OPEN;
    }

    public boolean isActualsClosed() {
        return actualsStatus == PlanStatus.CLOSED;
    }

    public boolean isActualsOpen() {
        return actualsStatus == PlanStatus.OPEN;
    }

    public ArrayList<ActualItem> getActualItems() { return actualItems; }

    public ArrayList<PlannedItem> getPlannedItemsForAccount(final String account) {
        ArrayList<PlannedItem> accountItems = new ArrayList<PlannedItem>();
        Iterator<PlannedItem> iter = plannedItems.iterator();
        while (iter.hasNext()) {
            PlannedItem curItem = iter.next();
            if (account.equals(curItem.getAccount())) {
                accountItems.add(curItem);
            }
        }
        return accountItems;
    }
}


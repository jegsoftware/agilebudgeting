package com.example.jonathon.agilebudgeting;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

/**
 * Created by Jonathon on 1/7/2017.
 */

public class Plan implements Serializable {
    private double amountToCapitalOne;
    private double amountToSavings;
    private PlanStatus planningStatus;
    private PlanStatus actualsStatus;
    private PlanningPeriod period;
    private ArrayList<PlannedItem> plannedItems;
    private ArrayList<Deposit> deposits;
    private long planId;
    private ArrayList<ActualItem> actualItems;
    private IPersistPlan persister;
    private boolean initializing;

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
        initializing = true;
    }

    public static Plan createPlan(PlanningPeriod planPeriod, IPersistPlan persister) {
        Plan newPlan = new Plan();
        newPlan.persister = persister;
        persister.populate(newPlan, planPeriod);
        newPlan.initializing = false;
        return newPlan;
    }

    public static Plan createPlan(Calendar planDate, IPersistPlan persister) {
        PlanningPeriod planPeriod = new PlanningPeriod(planDate);

        return createPlan(planPeriod, persister);
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

    private void persist() {
        if (initializing) return;

        persister.persist(this);
        return;
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


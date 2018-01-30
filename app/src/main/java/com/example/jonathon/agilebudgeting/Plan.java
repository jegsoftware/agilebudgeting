package com.example.jonathon.agilebudgeting;

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
    private ArrayList<Item> plannedItems;
    private ArrayList<Item> deposits;
    private PlanningPeriod planId;
    private ArrayList<Item> actualItems;
    private boolean initializing;
    private boolean isStoredInCloud;

    private Plan()
    {
        amountToCapitalOne = 0;
        amountToSavings = 0;
        planningStatus = PlanStatus.OPEN;
        actualsStatus = PlanStatus.OPEN;
        plannedItems = new ArrayList<Item>();
        deposits = new ArrayList<Item>();
        actualItems = new ArrayList<Item>();
        initializing = true;
        isStoredInCloud = false;
    }

    public static Plan createPlan(PlanningPeriod planPeriod, IPersistPlan persister) {
        Plan newPlan = new Plan();
        persister.populate(newPlan, planPeriod);
        newPlan.initializing = false;
        return newPlan;
    }

    public static Plan createPlan(Calendar planDate, IPersistPlan persister) {
        PlanningPeriod planPeriod = new PlanningPeriod(planDate);

        return createPlan(planPeriod, persister);
    }

    public String getPlanStartDate() {
        return period.getPeriodStartDate();
    }


    public void setPeriod (PlanningPeriod per) {
        period = per;
    }

    public void setPeriod (Calendar planDate) {
        period = new PlanningPeriod(planDate);
    }

    public void setPeriod (String planDate) throws ParseException {
        period = new PlanningPeriod();
        period.setDate(planDate);
    }

    public PlanningPeriod getPeriod () {
        return period;
    }

    public void openPlanning() {
        planningStatus = PlanStatus.OPEN;
    }

    public void closePlanning() {
        planningStatus = PlanStatus.CLOSED;
    }

    public void openActuals() {
        actualsStatus = PlanStatus.OPEN;
    }

    public void closeActuals() {
        actualsStatus = PlanStatus.CLOSED;
    }

    public void addPlannedItem(Item item) {
        plannedItems.add(item);
    }

    public void addDeposit(Item dep) {
        deposits.add(dep);
    }

    public void addActualItem(Item actualItem) {
        actualItems.add(actualItem);
    }

    public double getTotalPlannedExpenses() {
        double totalExpenses = 0.00;
        for (Item item: plannedItems) {
            totalExpenses += item.getAmount();
        }
        return totalExpenses;
    }

    public double getTotalDeposits() {
        double totalDeposits = 0.00;
        for (Item dep: deposits) {
            totalDeposits += dep.getAmount();
        }
        return totalDeposits;
    }

    public double getTotalActualExpenses() {
        double totalExpenses = 0.00;
        for (Item item: actualItems) {
            totalExpenses += item.getAmount();
        }
        return totalExpenses;
    }


    public double getNetPlannedAmount() {
        return getTotalDeposits() - getTotalPlannedExpenses();
    }

    public double getNetActualAmount() { return getTotalPlannedExpenses() - getTotalActualExpenses(); }

    public ArrayList<Item> getDeposits() {
        return deposits;
    }

    public ArrayList<Item> getPlannedItems() {
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

    public ArrayList<Item> getActualItems() { return actualItems; }

    public void setStoredInCloud(boolean storedInCloud) {
        isStoredInCloud = storedInCloud;
    }

    public boolean isStoredInCloud() {
        return isStoredInCloud;
    }

    public ArrayList<Item> getPlannedItemsForAccount(final String account) {
        ArrayList<Item> accountItems = new ArrayList<Item>();
        Iterator<Item> iter = plannedItems.iterator();
        while (iter.hasNext()) {
            Item curItem = iter.next();
            if (account.equals(curItem.getAccount())) {
                accountItems.add(curItem);
            }
        }
        return accountItems;
    }

    public void updateItem(Item editedItem) {
        ArrayList<Item> itemList;
        switch (editedItem.getType()) {
            case "PlannedItem" : itemList = plannedItems; break;
            case "ActualItem" : itemList = actualItems; break;
            case "Deposit" : itemList = deposits; break;
            default: itemList = new ArrayList();
        }

        for (int i = 0; i < itemList.size(); i++) {
            Item curItem = itemList.get(i);
            if (curItem.getItemId().equals(editedItem.getItemId())) {
                itemList.set(i, editedItem);
                break;
            }
        }
    }
}


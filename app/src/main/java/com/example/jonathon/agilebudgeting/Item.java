package com.example.jonathon.agilebudgeting;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Jonathon on 1/7/2017.
 */

public class Item implements Serializable {
    protected String description;
    protected double amount;
    protected String account; //TODO: Make account user-maintainable
    protected UUID itemId;
    protected PlanningPeriod planPeriod;
    protected String date;
    protected IPersistItem persister;
    protected String type;
    protected ArrayList<ActualItem> actualItems;

    protected Item() {
        date = "";
        actualItems = new ArrayList<ActualItem>();
    }

    public static Item createPlannedItem(PlanningPeriod planId, String desc, double amt, String acct, IPersistItem persister) {
        return createItem("PlannedItem", planId, desc, amt, acct, "", persister);
    }

    public static Item createItem(String type, PlanningPeriod planId, String desc, double amt, String acct, String date, IPersistItem persister) {
        Item newItem = new Item();

        newItem.setPlanPeriod(planId);
        newItem.setDescription(desc);
        newItem.setAmount(amt);
        newItem.setAccount(acct);
        newItem.setDate(date);
        newItem.persister = persister;
        newItem.type = type;
        newItem.itemId = UUID.randomUUID();
        persister.persist(newItem);

        return newItem;
    }

    public static Item createDeposit(PlanningPeriod planId, String date, String desc, double amt, String acct, IPersistItem persister) {
        return createItem("Deposit", planId, desc, amt, acct, date, persister);
    }

    public static Item createItem(UUID itemID, IPersistItem persister) {
        Item retrievedItem = persister.retrieve(itemID);

        retrievedItem.persister = persister;
        return retrievedItem;
    }

    public static Item createDeposit(UUID itemID, IPersistItem persister) {
        Item retrievedItem = persister.retrieve(itemID);
        Item deposit = new Item();
        deposit.type = "Deposit";
        deposit.setDate(retrievedItem.getDate());
        deposit.setDescription(retrievedItem.getDescription());
        deposit.setAmount(retrievedItem.getAmount());
        deposit.setAccount(retrievedItem.getAccount());
        deposit.setPlanPeriod(retrievedItem.getPlanPeriod());
        deposit.itemId = retrievedItem.getItemId();
        deposit.persister = persister;
        return deposit;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public String getAmountString() {
        return String.format("%1.2f", amount);
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public PlanningPeriod getPlanPeriod() {
        return planPeriod;
    }

    public void setPlanPeriod(PlanningPeriod planPeriod) {
        this.planPeriod = planPeriod;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public UUID getItemId() {
        return itemId;
    }

    public String getType() {
        return type;
    }

    public void persist() {
        persister.persist(this);
    }

    public void addActualItem(ActualItem actualItem) {
        if ((null != actualItem) && !actualItems.contains(actualItem)) {
            actualItems.add(actualItem);
            actualItem.addPlannedItem(this);
        }
    }
}

package com.example.jonathon.agilebudgeting;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
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
    protected String type;
    protected ArrayList<Item> relatedItems;

    protected Item() {
        date = "";
        relatedItems = new ArrayList<Item>();
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
        newItem.type = type;
        newItem.itemId = UUID.randomUUID();

        return newItem;
    }

    public static Item createItem(UUID itemID, IPersistItem persister) {
        Item retrievedItem = persister.retrieve(itemID);

        return retrievedItem;
    }

    public static Item createDeposit(PlanningPeriod planId, String date, String desc, double amt, String acct, IPersistItem persister) {
        return createItem("Deposit", planId, desc, amt, acct, date, persister);
    }

    public static Item createDeposit(UUID itemID, IPersistItem persister) {
        return createItem(itemID, persister);
    }

    public static Item createActualItem(PlanningPeriod planId, String date, String desc, double amt, String acct, IPersistItem persister) {
        return createItem("ActualItem", planId, desc, amt, acct, date, persister);
    }

    public static Item createActualItem(UUID itemID, IPersistItem persister) {
        Item retrievedItem = persister.retrieve(itemID);
        populateRelatedItems(retrievedItem, persister);

        return retrievedItem;
    }

    private static void populateRelatedItems(Item item,IPersistItem persister) {
        UUID[] itemIds = persister.retrieveRelatedItems(item);
        if (itemIds != null) {
            for (int i = 0; i < itemIds.length; i++) {
                item.relatedItems.add(Item.createItem(itemIds[i], persister));
            }
        }
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

    public void addRelatedItem(Item plannedItem) {
        if ((null != plannedItem) && !isMatchedTo(plannedItem)) {
            relatedItems.add(plannedItem);
        }
    }

    private boolean isMatchedTo(Item plannedItem) {
        if (relatedItems.contains(plannedItem)) return true;
        return hasMatch(plannedItem);
    }

    public boolean hasMatch(Item item) {
        Iterator<Item> iter = relatedItems.iterator();
        while (iter.hasNext()) {
            Item curItem = iter.next();
            if (item.getItemId().equals(curItem.getItemId())) {
                return true;
            }
        }

        return false;
    }
}

package com.example.jonathon.agilebudgeting;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Jonathon on 1/7/2017.
 */

public class PlannedItem extends Item {

    private ArrayList<ActualItem> actualItems;

    private PlannedItem() {
        description = "";
        amount = 0;
        account = "";
        actualItems = new ArrayList<ActualItem>();
    }

    public static PlannedItem createItem(PlanningPeriod planId, String desc, double amt, String acct, IPersistItem persister) {
        PlannedItem newItem = new PlannedItem();

        newItem.setDescription(desc);
        newItem.setAmount(amt);
        newItem.setAccount(acct);
        newItem.setPlanPeriod(planId);
        newItem.persister = persister;
        newItem.type = "PlannedItem";
        newItem.itemId = UUID.randomUUID();
        persister.persist(newItem);

        return newItem;
    }

    public static PlannedItem createItem(UUID itemID, IPersistItem persister) {
        Item retrievedItem = persister.retrieve(itemID);
        PlannedItem plannedItem = new PlannedItem();
        plannedItem.type = "PlannedItem";
        plannedItem.setDescription(retrievedItem.getDescription());
        plannedItem.setAmount(retrievedItem.getAmount());
        plannedItem.setAccount(retrievedItem.getAccount());
        plannedItem.setPlanPeriod(retrievedItem.getPlanPeriod());
        plannedItem.itemId = retrievedItem.getItemId();
        plannedItem.persister = persister;
        return plannedItem;
    }


    public void addActualItem(ActualItem actualItem) {
        if ((null != actualItem) && !actualItems.contains(actualItem)) {
            actualItems.add(actualItem);
            actualItem.addPlannedItem(this);
        }
    }
}

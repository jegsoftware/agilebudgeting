package com.example.jonathon.agilebudgeting;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Jonathon on 1/28/2017.
 */

public class ActualItem extends Item {

    private ArrayList<PlannedItem> plannedItems;

    private ActualItem() {
        super();
        description = "";
        amount = 0;
        account = "";
        planId = -1;
        itemId = -1;
        plannedItems = new ArrayList<PlannedItem>();
    }

    public static ActualItem createActualItem(long planId, String date, String desc, double amt, String acct, IPersistItem persister) {
        ActualItem newActualItem = new ActualItem();

        newActualItem.setDate(date);
        newActualItem.description = desc;
        newActualItem.amount = amt;
        newActualItem.account = acct;
        newActualItem.planId = planId;
        newActualItem.persister = persister;

        return newActualItem;

    }

    public static ActualItem createActualItem(long itemID, IPersistItem persister) {
        Item retrievedItem = persister.retrieve(itemID);
        ActualItem actualItem = new ActualItem();
        actualItem.type = "ActualItem";
        actualItem.setDate(retrievedItem.getDate());
        actualItem.setDescription(retrievedItem.getDescription());
        actualItem.setAmount(retrievedItem.getAmount());
        actualItem.setAccount(retrievedItem.getAccount());
        actualItem.setPlanId(retrievedItem.getPlanId());
        actualItem.itemId = retrievedItem.getItemId();
        actualItem.persister = persister;

        populatePlannedItems(actualItem);

        return actualItem;
    }

    private static void populatePlannedItems(ActualItem item) {
        long[] itemIds = item.persister.retrieveRelatedItems(item);
        for (int i = 0; i < itemIds.length; i++) {
            item.plannedItems.add(PlannedItem.createItem(itemIds[i],item.persister));
        }
    }

    public void addPlannedItem(PlannedItem plannedItem) {
        if ((null != plannedItem) && !isMatchedTo(plannedItem)) {
            plannedItems.add(plannedItem);
            plannedItem.addActualItem(this);
            saveRelationship(plannedItem);
        }
    }

    private boolean isMatchedTo(PlannedItem plannedItem) {
        if (plannedItems.contains(plannedItem)) return true;
        Iterator<PlannedItem> iter = plannedItems.iterator();
        while (iter.hasNext()) {
            PlannedItem curItem = iter.next();
            if (curItem.getItemId() == plannedItem.getItemId()) return true;
        }
        return false;
    }

    private void saveRelationship(PlannedItem plannedItem) {
        persister.persistRelationship(this, plannedItem);
    }

    public boolean hasMatch(PlannedItem item) {
        Iterator<PlannedItem> iter = plannedItems.iterator();
        while (iter.hasNext()) {
            PlannedItem curItem = iter.next();
            if (item.getItemId() == (curItem.getItemId())) {
                return true;
            }
        }

        return false;
    }
}

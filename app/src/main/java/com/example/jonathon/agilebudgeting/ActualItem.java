package com.example.jonathon.agilebudgeting;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

/**
 * Created by Jonathon on 1/28/2017.
 */

public class ActualItem extends Item {

    private ArrayList<Item> plannedItems;

    private ActualItem() {
        super();
        description = "";
        amount = 0;
        account = "";
        plannedItems = new ArrayList<Item>();
    }

    public static ActualItem createActualItem(PlanningPeriod planId, String date, String desc, double amt, String acct, IPersistItem persister) {
        ActualItem newActualItem = new ActualItem();

        newActualItem.setDate(date);
        newActualItem.description = desc;
        newActualItem.amount = amt;
        newActualItem.account = acct;
        newActualItem.planPeriod = planId;
        newActualItem.persister = persister;
        newActualItem.type = "ActualItem";
        newActualItem.itemId = UUID.randomUUID();
        persister.persist(newActualItem);

        return newActualItem;

    }

    public static ActualItem createActualItem(UUID itemID, IPersistItem persister) {
        Item retrievedItem = persister.retrieve(itemID);
        ActualItem actualItem = new ActualItem();
        actualItem.type = "ActualItem";
        actualItem.setDate(retrievedItem.getDate());
        actualItem.setDescription(retrievedItem.getDescription());
        actualItem.setAmount(retrievedItem.getAmount());
        actualItem.setAccount(retrievedItem.getAccount());
        actualItem.setPlanPeriod(retrievedItem.getPlanPeriod());
        actualItem.itemId = retrievedItem.getItemId();
        actualItem.persister = persister;

        populatePlannedItems(actualItem);

        return actualItem;
    }

    private static void populatePlannedItems(ActualItem item) {
        UUID[] itemIds = item.persister.retrieveRelatedItems(item);
        for (int i = 0; i < itemIds.length; i++) {
            item.plannedItems.add(Item.createItem(itemIds[i],item.persister));
        }
    }

    public void addPlannedItem(Item plannedItem) {
        if ((null != plannedItem) && !isMatchedTo(plannedItem)) {
            plannedItems.add(plannedItem);
            plannedItem.addActualItem(this);
            saveRelationship(plannedItem);
        }
    }

    private boolean isMatchedTo(Item plannedItem) {
        if (plannedItems.contains(plannedItem)) return true;
        return hasMatch(plannedItem);
    }

    private void saveRelationship(Item plannedItem) {
        persister.persistRelationship(this, plannedItem);
    }

    public boolean hasMatch(Item item) {
        Iterator<Item> iter = plannedItems.iterator();
        while (iter.hasNext()) {
            Item curItem = iter.next();
            if (item.getItemId().equals(curItem.getItemId())) {
                return true;
            }
        }

        return false;
    }
}

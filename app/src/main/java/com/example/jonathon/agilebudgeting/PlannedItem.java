package com.example.jonathon.agilebudgeting;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jonathon on 1/7/2017.
 */

public class PlannedItem extends Item {

    private ArrayList<ActualItem> actualItems;

    private PlannedItem() {
        description = "";
        amount = 0;
        account = "";
        planId = -1;
        itemId = -1;
        actualItems = new ArrayList<ActualItem>();
    }

    public static PlannedItem createItem(long planId, String desc, double amt, String acct, IPersistItem persister) {
        PlannedItem newItem = new PlannedItem();

        newItem.setDescription(desc);
        newItem.setAmount(amt);
        newItem.setAccount(acct);
        newItem.setPlanId(planId);
        newItem.persister = persister;

        return newItem;
    }

    public static PlannedItem createItem(long itemID, IPersistItem persister) {
        Item retrievedItem = persister.retrieve(itemID);
        PlannedItem plannedItem = new PlannedItem();
        plannedItem.type = "PlannedItem";
        plannedItem.setDescription(retrievedItem.getDescription());
        plannedItem.setAmount(retrievedItem.getAmount());
        plannedItem.setAccount(retrievedItem.getAccount());
        plannedItem.setPlanId(retrievedItem.getPlanId());
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

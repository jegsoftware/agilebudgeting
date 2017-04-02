package com.example.jonathon.agilebudgeting;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
        AgileBudgetingDbHelper dbHelper = DbHelperSingleton.getInstance().getDbHelper();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                AgileBudgetingContract.Match.COLUMN_NAME_PLANNED_ID
        };

        String selection = AgileBudgetingContract.Match.COLUMN_NAME_ACTUAL_ID + " = ?";
        String[] selectionArgs = { String.valueOf(item.getItemId()) };

        Cursor cursor = db.query(
                AgileBudgetingContract.Match.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            long plannedItemId = cursor.getLong(cursor.getColumnIndex(AgileBudgetingContract.Match.COLUMN_NAME_PLANNED_ID));
            item.plannedItems.add(PlannedItem.createItem(plannedItemId,item.persister));
            cursor.moveToNext();
        }
        cursor.close();
        db.close();

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
        AgileBudgetingDbHelper dbHelper = DbHelperSingleton.getInstance().getDbHelper();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AgileBudgetingContract.Match.COLUMN_NAME_ACTUAL_ID, itemId);
        values.put(AgileBudgetingContract.Match.COLUMN_NAME_PLANNED_ID, plannedItem.getItemId());

        db.insert(AgileBudgetingContract.Match.TABLE_NAME, null, values);
        db.close();
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

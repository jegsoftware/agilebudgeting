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

    public static ActualItem createActualItem(long planId, String date, String desc, double amt, String acct) {
        ActualItem newActualItem = new ActualItem();

        newActualItem.setDate(date);
        newActualItem.description = desc;
        newActualItem.amount = amt;
        newActualItem.account = acct;
        newActualItem.planId = planId;

        return newActualItem;

    }

    public static ActualItem createActualItem(long itemID) {
        ActualItem newActualItem = new ActualItem();

        AgileBudgetingDbHelper dbHelper = DbHelperSingleton.getInstance().getDbHelper();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                AgileBudgetingContract.Items.COLUMN_NAME_DATE,
                AgileBudgetingContract.Items.COLUMN_NAME_DESCRIPTION,
                AgileBudgetingContract.Items.COLUMN_NAME_AMOUNT,
                AgileBudgetingContract.Items.COLUMN_NAME_ACCOUNT,
                AgileBudgetingContract.Items.COLUMN_NAME_PLANID,
        };

        String selection = AgileBudgetingContract.Items._ID + " = ?";
        String[] selectionArgs = { String.valueOf(itemID) };

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
        String date = cursor.getString(cursor.getColumnIndex(AgileBudgetingContract.Items.COLUMN_NAME_DATE));
        String desc = cursor.getString(cursor.getColumnIndex(AgileBudgetingContract.Items.COLUMN_NAME_DESCRIPTION));
        double amt = cursor.getDouble(cursor.getColumnIndex(AgileBudgetingContract.Items.COLUMN_NAME_AMOUNT));
        String acct = cursor.getString(cursor.getColumnIndex(AgileBudgetingContract.Items.COLUMN_NAME_ACCOUNT));
        long planId = cursor.getLong(cursor.getColumnIndex(AgileBudgetingContract.Items.COLUMN_NAME_PLANID));
        cursor.close();

        newActualItem.setDate(date);
        newActualItem.description = desc;
        newActualItem.amount = amt;
        newActualItem.account = acct;
        newActualItem.planId = planId;
        newActualItem.itemId = itemID;
        populatePlannedItems(db, newActualItem);

        db.close();

        return newActualItem;

    }

    private static void populatePlannedItems(SQLiteDatabase db, ActualItem item) {
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
            item.plannedItems.add(PlannedItem.createItem(plannedItemId));
            cursor.moveToNext();
        }
        cursor.close();

    }

    public long persist() {
        AgileBudgetingDbHelper dbHelper = DbHelperSingleton.getInstance().getDbHelper();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AgileBudgetingContract.Items.COLUMN_NAME_PLANID, planId);
        values.put(AgileBudgetingContract.Items.COLUMN_NAME_DATE, getDate());
        values.put(AgileBudgetingContract.Items.COLUMN_NAME_DESCRIPTION, description);
        values.put(AgileBudgetingContract.Items.COLUMN_NAME_AMOUNT, amount);
        values.put(AgileBudgetingContract.Items.COLUMN_NAME_ACCOUNT, account);
        values.put(AgileBudgetingContract.Items.COLUMN_NAME_TYPE, "ActualItem");

        if (itemId == -1) {
            itemId = db.insert(AgileBudgetingContract.Items.TABLE_NAME, null, values);
        }
        else {
            String selection = AgileBudgetingContract.Items._ID + " = ?";
            String[] selectionArgs = { String.valueOf(itemId) };

            db.update(AgileBudgetingContract.Items.TABLE_NAME, values, selection, selectionArgs);
        }
        db.close();
        return itemId;
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

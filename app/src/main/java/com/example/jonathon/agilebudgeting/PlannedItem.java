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

    public static PlannedItem createItem(long planId, String desc, double amt, String acct) {
        PlannedItem newItem = new PlannedItem();

        newItem.setDescription(desc);
        newItem.setAmount(amt);
        newItem.setAccount(acct);
        newItem.setPlanId(planId);

        return newItem;
    }

    public static PlannedItem createItem(Context context, long itemID) {
        AgileBudgetingDbHelper dbHelper = new AgileBudgetingDbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
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
        String desc = cursor.getString(cursor.getColumnIndex(AgileBudgetingContract.Items.COLUMN_NAME_DESCRIPTION));
        double amt = cursor.getDouble(cursor.getColumnIndex(AgileBudgetingContract.Items.COLUMN_NAME_AMOUNT));
        String acct = cursor.getString(cursor.getColumnIndex(AgileBudgetingContract.Items.COLUMN_NAME_ACCOUNT));
        long planId = cursor.getLong(cursor.getColumnIndex(AgileBudgetingContract.Items.COLUMN_NAME_PLANID));

        db.close();

        PlannedItem newItem = new PlannedItem();
        newItem.setDescription(desc);
        newItem.setAmount(amt);
        newItem.setAccount(acct);
        newItem.setPlanId(planId);
        newItem.itemId = itemID;

        return newItem;
    }


    @Override
    public long persist(Context context) {
        AgileBudgetingDbHelper dbHelper = new AgileBudgetingDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AgileBudgetingContract.Items.COLUMN_NAME_PLANID, planId);
        values.put(AgileBudgetingContract.Items.COLUMN_NAME_DESCRIPTION, description);
        values.put(AgileBudgetingContract.Items.COLUMN_NAME_AMOUNT, amount);
        values.put(AgileBudgetingContract.Items.COLUMN_NAME_ACCOUNT, account);
        values.put(AgileBudgetingContract.Items.COLUMN_NAME_TYPE, "PlannedItem");

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

    public void addActualItem(Context context, ActualItem actualItem) {
        if ((null != actualItem) && !actualItems.contains(actualItem)) {
            actualItems.add(actualItem);
            actualItem.addPlannedItem(context, this);
        }
    }
}

package com.example.jonathon.agilebudgeting;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.Serializable;

/**
 * Created by Jonathon on 3/4/2017.
 */

public class DBItemPersister implements IPersistItem, Serializable {
    @Override
    public long persist(Item item) {

        AgileBudgetingDbHelper dbHelper = DbHelperSingleton.getInstance().getDbHelper();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AgileBudgetingContract.Items.COLUMN_NAME_PLANID, item.getPlanId());
        values.put(AgileBudgetingContract.Items.COLUMN_NAME_DATE, item.getDate());
        values.put(AgileBudgetingContract.Items.COLUMN_NAME_DESCRIPTION, item.getDescription());
        values.put(AgileBudgetingContract.Items.COLUMN_NAME_AMOUNT, item.getAmount());
        values.put(AgileBudgetingContract.Items.COLUMN_NAME_ACCOUNT, item.getAccount());
        values.put(AgileBudgetingContract.Items.COLUMN_NAME_TYPE, item.getType());

        long itemId = item.getItemId();
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

    @Override
    public Item retrieve(long itemId) {
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
        String[] selectionArgs = { String.valueOf(itemId) };

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
        db.close();

        Item newItem = new Item();
        newItem.date = date;
        newItem.setDescription(desc);
        newItem.setAmount(amt);
        newItem.setAccount(acct);
        newItem.setPlanId(planId);
        newItem.itemId = itemId;
        newItem.persister = this;
        return newItem;
    }
}

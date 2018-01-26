package com.example.jonathon.agilebudgeting;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by Jonathon on 3/4/2017.
 */

public class DBItemPersister implements IPersistItem, Serializable {

    public static final String SELECTION =
            AgileBudgetingContract.Items.COLUMN_NAME_ITEMUUID + " = ?";

    @Override
    public void persist(Item item) {

        AgileBudgetingDbHelper dbHelper = DbHelperSingleton.getInstance().getDbHelper();
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String itemId = item.getItemId().toString();

        ContentValues values = new ContentValues();
        values.put(AgileBudgetingContract.Items.COLUMN_NAME_PERIODNUM, item.getPlanPeriod().getPeriodNumber());
        values.put(AgileBudgetingContract.Items.COLUMN_NAME_PERIODYEAR, item.getPlanPeriod().getPeriodYear());
        values.put(AgileBudgetingContract.Items.COLUMN_NAME_DATE, item.getDate());
        values.put(AgileBudgetingContract.Items.COLUMN_NAME_DESCRIPTION, item.getDescription());
        values.put(AgileBudgetingContract.Items.COLUMN_NAME_AMOUNT, item.getAmount());
        values.put(AgileBudgetingContract.Items.COLUMN_NAME_ACCOUNT, item.getAccount());
        values.put(AgileBudgetingContract.Items.COLUMN_NAME_TYPE, item.getType());
        values.put(AgileBudgetingContract.Items.COLUMN_NAME_ITEMUUID, itemId);

        String[] selectionArgs = { itemId  };

        Cursor cursor = findItem(db, selectionArgs);

        if (cursor.isAfterLast()) {
            db.insert(AgileBudgetingContract.Items.TABLE_NAME, null, values);
        } else {
            db.update(AgileBudgetingContract.Items.TABLE_NAME, values, SELECTION, selectionArgs);
        }
        cursor.close();
        db.close();
    }

    @Override
    public void persistRelationship(Item item1, Item item2) {
        AgileBudgetingDbHelper dbHelper = DbHelperSingleton.getInstance().getDbHelper();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AgileBudgetingContract.Match.COLUMN_NAME_ACTUAL_ID, item1.getItemId().toString());
        values.put(AgileBudgetingContract.Match.COLUMN_NAME_PLANNED_ID, item2.getItemId().toString());

        db.insert(AgileBudgetingContract.Match.TABLE_NAME, null, values);
        db.close();

    }

    @Override
    public Item retrieve(UUID itemId) {
        AgileBudgetingDbHelper dbHelper = DbHelperSingleton.getInstance().getDbHelper();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] selectionArgs = { itemId.toString() };
        Cursor cursor = findItem(db, selectionArgs);

        String date = cursor.getString(cursor.getColumnIndex(AgileBudgetingContract.Items.COLUMN_NAME_DATE));
        String desc = cursor.getString(cursor.getColumnIndex(AgileBudgetingContract.Items.COLUMN_NAME_DESCRIPTION));
        double amt = cursor.getDouble(cursor.getColumnIndex(AgileBudgetingContract.Items.COLUMN_NAME_AMOUNT));
        String acct = cursor.getString(cursor.getColumnIndex(AgileBudgetingContract.Items.COLUMN_NAME_ACCOUNT));
        int perNum = cursor.getInt(cursor.getColumnIndex(AgileBudgetingContract.Items.COLUMN_NAME_PERIODNUM));
        int perYear = cursor.getInt(cursor.getColumnIndex(AgileBudgetingContract.Items.COLUMN_NAME_PERIODYEAR));
        String type = cursor.getString(cursor.getColumnIndex(AgileBudgetingContract.Items.COLUMN_NAME_TYPE));

        cursor.close();
        db.close();

        Item newItem = new Item();
        newItem.type = type;
        newItem.date = date;
        newItem.setDescription(desc);
        newItem.setAmount(amt);
        newItem.setAccount(acct);
        newItem.setPlanPeriod(new PlanningPeriod(perNum, perYear));
        newItem.itemId = itemId;
        return newItem;
    }

    @Override
    public UUID[] retrieveRelatedItems(Item item) {
        AgileBudgetingDbHelper dbHelper = DbHelperSingleton.getInstance().getDbHelper();
        SQLiteDatabase db = dbHelper.getWritableDatabase();

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
        UUID[] items = new UUID[cursor.getCount()];
        int idx = 0;
        while (!cursor.isAfterLast()) {
            String plannedItemId = cursor.getString(cursor.getColumnIndex(AgileBudgetingContract.Match.COLUMN_NAME_PLANNED_ID));
            items[idx++] = UUID.fromString(plannedItemId);
            cursor.moveToNext();
        }
        cursor.close();
        db.close();

        return items;
    }

    private Cursor findItem(SQLiteDatabase db, String[] selectionArgs) {

        String[] projection = {
                AgileBudgetingContract.Items.COLUMN_NAME_DATE,
                AgileBudgetingContract.Items.COLUMN_NAME_DESCRIPTION,
                AgileBudgetingContract.Items.COLUMN_NAME_AMOUNT,
                AgileBudgetingContract.Items.COLUMN_NAME_ACCOUNT,
                AgileBudgetingContract.Items.COLUMN_NAME_PERIODNUM,
                AgileBudgetingContract.Items.COLUMN_NAME_PERIODYEAR,
                AgileBudgetingContract.Items.COLUMN_NAME_ITEMUUID,
                AgileBudgetingContract.Items.COLUMN_NAME_TYPE,
        };

        Cursor cursor = db.query(
                AgileBudgetingContract.Items.TABLE_NAME,
                projection,
                SELECTION,
                selectionArgs,
                null,
                null,
                null
        );

        cursor.moveToFirst();
        return cursor;
    }
}

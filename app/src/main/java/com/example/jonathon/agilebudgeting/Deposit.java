package com.example.jonathon.agilebudgeting;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Jonathon on 1/20/2017.
 */

public class Deposit extends Item {

    private Deposit() {
        description = "";
        amount = 0;
        account = "";
        planId = -1;
        itemId = -1;
    }

    public static Deposit createDeposit(long planId, String date, String desc, double amt, String acct) {
        Deposit newDeposit = new Deposit();

        newDeposit.date = date;
        newDeposit.description = desc;
        newDeposit.amount = amt;
        newDeposit.account = acct;
        newDeposit.planId = planId;

        return newDeposit;

    }

    public static Deposit createDeposit(long itemID) {
        Deposit newDeposit = new Deposit();

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
        db.close();

        newDeposit.date = date;
        newDeposit.description = desc;
        newDeposit.amount = amt;
        newDeposit.account = acct;
        newDeposit.planId = planId;
        newDeposit.itemId = itemID;

        return newDeposit;

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    @Override
    public long persist() {
        AgileBudgetingDbHelper dbHelper = DbHelperSingleton.getInstance().getDbHelper();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AgileBudgetingContract.Items.COLUMN_NAME_PLANID, planId);
        values.put(AgileBudgetingContract.Items.COLUMN_NAME_DATE, date);
        values.put(AgileBudgetingContract.Items.COLUMN_NAME_DESCRIPTION, description);
        values.put(AgileBudgetingContract.Items.COLUMN_NAME_AMOUNT, amount);
        values.put(AgileBudgetingContract.Items.COLUMN_NAME_ACCOUNT, account);
        values.put(AgileBudgetingContract.Items.COLUMN_NAME_TYPE, "Deposit");

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

}

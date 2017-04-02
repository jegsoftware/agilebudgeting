package com.example.jonathon.agilebudgeting;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Jonathon on 1/21/2017.
 */

public class AgileBudgetingDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 6;
    public static final String DATABASE_NAME = "AgileBudgeting.db";

    private static final String SQL_CREATE_PLANS_TABLE =
            "CREATE TABLE " + AgileBudgetingContract.Plans.TABLE_NAME + " (" +
                    AgileBudgetingContract.Plans._ID + " INTEGER PRIMARY KEY," +
                    AgileBudgetingContract.Plans.COLUMN_NAME_PERIODNUM + " INTEGER," +
                    AgileBudgetingContract.Plans.COLUMN_NAME_PERIODYEAR + " INTEGER," +
                    AgileBudgetingContract.Plans.COLUMN_NAME_PLANNING_STATUS + " TEXT," +
                    AgileBudgetingContract.Plans.COLUMN_NAME_ACTUALS_STATUS + " TEXT)";

    private static final String SQL_CREATE_ITEMS_TABLE =
            "CREATE TABLE " + AgileBudgetingContract.Items.TABLE_NAME + " (" +
                    AgileBudgetingContract.Items._ID + " INTEGER PRIMARY KEY," +
                    AgileBudgetingContract.Items.COLUMN_NAME_ITEMUUID + " TEXT," +
                    AgileBudgetingContract.Items.COLUMN_NAME_PERIODNUM + " INTEGER," +
                    AgileBudgetingContract.Items.COLUMN_NAME_PERIODYEAR + " INTEGER," +
                    AgileBudgetingContract.Items.COLUMN_NAME_DESCRIPTION + " TEXT," +
                    AgileBudgetingContract.Items.COLUMN_NAME_AMOUNT + " REAL," +
                    AgileBudgetingContract.Items.COLUMN_NAME_ACCOUNT + " TEXT," +
                    AgileBudgetingContract.Items.COLUMN_NAME_DATE + " TEXT," +
                    AgileBudgetingContract.Items.COLUMN_NAME_TYPE + " TEXT)";

    private static final String SQL_CREATE_MATCHING_TABLE =
            "CREATE TABLE " + AgileBudgetingContract.Match.TABLE_NAME + " (" +
                    AgileBudgetingContract.Match._ID + " INTEGER PRIMARY KEY," +
                    AgileBudgetingContract.Match.COLUMN_NAME_ACTUAL_ID + " TEXT," +
                    AgileBudgetingContract.Match.COLUMN_NAME_PLANNED_ID + " TEXT)";

    private static final String SQL_DELETE_PLANS_TABLE =
            "DROP TABLE IF EXISTS " + AgileBudgetingContract.Plans.TABLE_NAME;

    private static final String SQL_DELETE_MATCHING_TABLE =
            "DROP TABLE IF EXISTS " + AgileBudgetingContract.Match.TABLE_NAME;

    private static final String SQL_DELETE_ITEMS_TABLE =
            "DROP TABLE IF EXISTS " + AgileBudgetingContract.Items.TABLE_NAME;

    public AgileBudgetingDbHelper(Context context) {
        super(context, DATABASE_NAME, null , DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_PLANS_TABLE);
        db.execSQL(SQL_CREATE_ITEMS_TABLE);
        db.execSQL(SQL_CREATE_MATCHING_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ITEMS_TABLE);
        db.execSQL(SQL_DELETE_PLANS_TABLE);
        db.execSQL(SQL_DELETE_MATCHING_TABLE);
        onCreate(db);
    }
}

package com.example.jonathon.agilebudgeting;

import android.content.Context;

/**
 * Created by Jonathon on 2/25/2017.
 */

public class DbHelperSingleton {

    private static DbHelperSingleton instance;
    private AgileBudgetingDbHelper dbHelper;

    public static DbHelperSingleton getInstance() {
        if (null == instance) instance = getSync();
        return instance;
    }

    private static synchronized DbHelperSingleton getSync() {
        if (null == instance) instance = new DbHelperSingleton();
        return instance;
    }

    public void init(Context context) {
        if (null == dbHelper) dbHelper = new AgileBudgetingDbHelper(context);
    }

    public AgileBudgetingDbHelper getDbHelper() {
        return dbHelper;
    }

    private void setDbHelper(AgileBudgetingDbHelper dbHelper) { this.dbHelper = dbHelper; }
}

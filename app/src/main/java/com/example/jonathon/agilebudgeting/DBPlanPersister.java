package com.example.jonathon.agilebudgeting;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by Jonathon on 3/4/2017.
 */

public class DBPlanPersister implements IPersistPlan,Serializable {

    public static final String SELECTION =
            AgileBudgetingContract.Plans.COLUMN_NAME_PERIODNUM + " = ? AND " +
            AgileBudgetingContract.Plans.COLUMN_NAME_PERIODYEAR + " = ?";

    @Override
    public void persist(Plan plan) {

        AgileBudgetingDbHelper dbHelper = DbHelperSingleton.getInstance().getDbHelper();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        PlanningPeriod period = plan.getPeriod();
        PlanStatus planningStatus = plan.isPlanningOpen() ? PlanStatus.OPEN : PlanStatus.CLOSED;
        PlanStatus actualsStatus = plan.isActualsOpen() ? PlanStatus.OPEN : PlanStatus.CLOSED;
        values.put(AgileBudgetingContract.Plans.COLUMN_NAME_PERIODNUM, period.getPeriodNumber());
        values.put(AgileBudgetingContract.Plans.COLUMN_NAME_PERIODYEAR, period.getPeriodYear());
        values.put(AgileBudgetingContract.Plans.COLUMN_NAME_PLANNING_STATUS, planningStatus.name());
        values.put(AgileBudgetingContract.Plans.COLUMN_NAME_ACTUALS_STATUS, actualsStatus.name());

        String[] selectionArgs = {
                String.valueOf(period.getPeriodNumber()),
                String.valueOf(period.getPeriodYear())
        };

        Cursor cursor = findPlan(db, selectionArgs);

        if (cursor.isAfterLast()) {
            db.insert(AgileBudgetingContract.Plans.TABLE_NAME, null, values);
        } else {
            db.update(AgileBudgetingContract.Plans.TABLE_NAME, values, SELECTION, selectionArgs);
        }
        cursor.close();
        db.close();
    }

    @Override
    public Plan populate(Plan newPlan, PlanningPeriod period) {
        AgileBudgetingDbHelper dbHelper = DbHelperSingleton.getInstance().getDbHelper();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] selectionArgs = {
                String.valueOf(period.getPeriodNumber()),
                String.valueOf(period.getPeriodYear())
        };

        Cursor cursor = findPlan(db, selectionArgs);

        if (cursor.isAfterLast()) {
            newPlan.setPeriod(period);
            cursor.close();
        }
        else {
            String planningStatusString = cursor.getString(cursor.getColumnIndex(AgileBudgetingContract.Plans.COLUMN_NAME_PLANNING_STATUS));
            String actualsStatusString = cursor.getString(cursor.getColumnIndex(AgileBudgetingContract.Plans.COLUMN_NAME_ACTUALS_STATUS));
            int perNum = cursor.getInt(cursor.getColumnIndex(AgileBudgetingContract.Plans.COLUMN_NAME_PERIODNUM));
            int perYear = cursor.getInt(cursor.getColumnIndex(AgileBudgetingContract.Plans.COLUMN_NAME_PERIODYEAR));

            if(PlanStatus.CLOSED == PlanStatus.valueOf(planningStatusString)) {
                newPlan.closePlanning();
            }
            if(PlanStatus.CLOSED == PlanStatus.valueOf(actualsStatusString)) {
                newPlan.closeActuals();
            }
            newPlan.setPeriod(new PlanningPeriod(perNum, perYear));
            populateItems(newPlan);
            cursor.close();
        }
        db.close();
        return newPlan;
    }

    private Cursor findPlan(SQLiteDatabase db, String[] selectionArgs) {

        String[] projection = {
                AgileBudgetingContract.Plans._ID,
                AgileBudgetingContract.Plans.COLUMN_NAME_PLANNING_STATUS,
                AgileBudgetingContract.Plans.COLUMN_NAME_ACTUALS_STATUS,
                AgileBudgetingContract.Plans.COLUMN_NAME_PERIODYEAR,
                AgileBudgetingContract.Plans.COLUMN_NAME_PERIODNUM,
        };

        Cursor cursor = db.query(
                AgileBudgetingContract.Plans.TABLE_NAME,
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

    private void populateItems(Plan newPlan) {
        AgileBudgetingDbHelper dbHelper = DbHelperSingleton.getInstance().getDbHelper();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                AgileBudgetingContract.Items.COLUMN_NAME_ITEMUUID,
                AgileBudgetingContract.Items.COLUMN_NAME_TYPE,
        };

        String selection =
                AgileBudgetingContract.Items.COLUMN_NAME_PERIODNUM + " = ? AND " +
                AgileBudgetingContract.Items.COLUMN_NAME_PERIODYEAR + " = ?";

        String[] selectionArgs = {
                String.valueOf(newPlan.getPeriod().getPeriodNumber()),
                String.valueOf(newPlan.getPeriod().getPeriodYear())
        };

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
        while (!cursor.isAfterLast()) {
            String type = cursor.getString(cursor.getColumnIndex(AgileBudgetingContract.Items.COLUMN_NAME_TYPE));
            String itemIdStr = cursor.getString(cursor.getColumnIndex(AgileBudgetingContract.Items.COLUMN_NAME_ITEMUUID));
            UUID itemId = UUID.fromString(itemIdStr);

            if ("Deposit".equals(type)) {
                Item deposit = Item.createDeposit(itemId, new DBItemPersister());
                newPlan.addDeposit(deposit);
            }
            else if ("PlannedItem".equals(type)) {
                Item plannedItem = Item.createItem(itemId, new DBItemPersister());
                newPlan.addPlannedItem(plannedItem);
            }
            else if ("ActualItem".equals(type)) {
                Item actualItem = Item.createActualItem(itemId, new DBItemPersister());
                newPlan.addActualItem(actualItem);
            }

            cursor.moveToNext();
        }
        cursor.close();
    }


}

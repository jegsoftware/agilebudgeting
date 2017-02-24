package com.example.jonathon.agilebudgeting;

import android.provider.BaseColumns;

/**
 * DB Schema contract for the tables in Agile Budgeting
 */

public final class AgileBudgetingContract {

    private AgileBudgetingContract() {}

    public static class Plans implements BaseColumns {
        public static final String TABLE_NAME = "plans";
        public static final String COLUMN_NAME_PERIODNUM = "periodnum";
        public static final String COLUMN_NAME_PERIODYEAR = "periodyear";
        public static final String COLUMN_NAME_PLANNING_STATUS = "planningstatus";
        public static final String COLUMN_NAME_ACTUALS_STATUS = "actualsstatus";
    }

    public static final class Items implements BaseColumns {
        public static final String TABLE_NAME = "items";
        public static final String COLUMN_NAME_PLANID = "planid";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_AMOUNT = "amount";
        public static final String COLUMN_NAME_ACCOUNT = "account";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_TYPE = "type";
    }

    public static final class Match implements BaseColumns {
        public static final String TABLE_NAME = "match";
        public static final String COLUMN_NAME_ACTUAL_ID = "actualitemid";
        public static final String COLUMN_NAME_PLANNED_ID = "planneditemid";
    }
}

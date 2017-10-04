package com.jvanpelt.whatsleft;

import android.provider.BaseColumns;

/**
 * Created by blumojo on 10/1/17.
 */

public final class TransactionsContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private TransactionsContract() {}

    /* Inner class that defines the table contents */
    public static class TransactionEntry implements BaseColumns {
        public static final String TABLE_NAME = "transactions";
        public static final String COLUMN_NAME = "name";
        public static final String HAS_CLEARED = "has_cleared";
        public static final String VALUE = "value";
        public static final String _ID = "_id";
        //public static final String REOCCURANCE = "reoccurance";
        //public static final String START_DATE = "start_date";

        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE IF NOT EXISTS " + TransactionEntry.TABLE_NAME + " (" +
                        TransactionEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        TransactionEntry.COLUMN_NAME + " TEXT," +
                        TransactionEntry.VALUE + " TEXT," +
                        TransactionEntry.HAS_CLEARED + " INTEGER)";
                        //TransactionEntry.REOCCURANCE + "TEXT" +
                        //TransactionEntry.START_DATE + "TEXT)";

        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TransactionEntry.TABLE_NAME;
    }


    public static class Current implements BaseColumns {
        public static final String TABLE_NAME = "current";
        public static final String COLUMN_VALUE = "value";
        public static final String _ID = "_id";

        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                _ID + " INTEGER, " +
                COLUMN_VALUE + " TEXT" + ")";

        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + Current.TABLE_NAME;
    }

}


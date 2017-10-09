package com.jvanpelt.whatsleft;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by blumojo on 10/1/17.
 */

public class TransactionDbHelper extends SQLiteOpenHelper {
    private String TAG = "TransactionDbHelper";
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Transactions.db";

    public TransactionDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TransactionsContract.TransactionEntry.SQL_CREATE_ENTRIES);
        db.execSQL(TransactionsContract.Current.SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // not caching online data - so the below will not work
        //db.execSQL(TransactionsContract.TransactionEntry.SQL_DELETE_ENTRIES);
        //onCreate(db);
        // will need to Add ALTER commands etc.
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public ArrayList<ModelTrans> getAllTrans(Context context) {
        try {
            String query = "SELECT * FROM " + TransactionsContract.TransactionEntry.TABLE_NAME;
            ArrayList<ModelTrans> transactions = new ArrayList<>();
            SQLiteDatabase database = new TransactionDbHelper(context).getReadableDatabase();
            Cursor c = database.rawQuery(query, null);
            if (c != null) {
                while (c.moveToNext()) {
                    String name = c.getString(c.getColumnIndex(
                            TransactionsContract.TransactionEntry.COLUMN_NAME));

                    String value = c.getString(c.getColumnIndex(
                            TransactionsContract.TransactionEntry.VALUE));

                    String has_cleared = c.getString(c.getColumnIndex(
                            TransactionsContract.TransactionEntry.HAS_CLEARED));

                    ModelTrans t = new ModelTrans();
                    t.setName(name);
                    t.setValue(Float.parseFloat(value));
                    int clr = Integer.parseInt(has_cleared);
                    if (clr > 0)
                        t.setCleared(true);
                    else
                        t.setCleared(false);

                    //Log.v("DBHelper: ", "Name: " + name);
                    //Log.v("DBHelper: ", "Value: " + value);
                    //Log.v("DBHelper: ", "HasCleared: " + has_cleared);

                    transactions.add(t);
                }
            }
            //database.close();
            return transactions;
        }
        catch (Exception e) {
            Log.e(TAG, e.toString());
            return null;
        }
    }

    public void DeleteTransaction(Context context, int pos)
    {
        try {
            SQLiteDatabase database = new TransactionDbHelper(context).getWritableDatabase();
            String idholder = "";

            // get the ID number
            int cnt = 0;
            String query = "SELECT * FROM " + TransactionsContract.TransactionEntry.TABLE_NAME;
            Cursor c = database.rawQuery(query, null);
            if (c != null) {
                while (c.moveToNext()) {
                    if (cnt == pos) {
                        idholder = c.getString(c.getColumnIndex(
                                TransactionsContract.TransactionEntry._ID));
                    }
                    cnt++;
                }
            }

            // now delete it
            query = "DELETE FROM " + TransactionsContract.TransactionEntry.TABLE_NAME +
                    " WHERE _id = " + idholder + "";
            database.execSQL(query);
            //database.close();
        }
        catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    public void UpdateTutorial(Context context, boolean Enable)
    {
        try {
            // Gets the data repository in write mode
            SQLiteDatabase db = new TransactionDbHelper(context).getWritableDatabase();

            // remove all previous entries
            String query = "DELETE FROM " + TransactionsContract.Current.TABLE_NAME +
                    " WHERE _id=2";
            db.execSQL(query);

            // now add the new value in
            String val = Boolean.toString(Enable);
            ContentValues values = new ContentValues();
            values.put(TransactionsContract.Current._ID, 1);
            values.put(TransactionsContract.Current.COLUMN_VALUE, val);
            db.replace(TransactionsContract.Current.TABLE_NAME,
                    null, values);
            //db.close();
        }
        catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    public boolean GetTutorialEnabled(Context context)
    {
        // read in the values from the database
        boolean first = true;
        try {
            SQLiteDatabase database = new TransactionDbHelper(context).getReadableDatabase();
            String query = "SELECT * FROM " + TransactionsContract.Current.TABLE_NAME + " WHERE " +
                    "_id=2";
            Cursor c = database.rawQuery(query, null);
            if (c != null) {
                while (c.moveToNext()) {
                    String val = c.getString(c.getColumnIndex(
                            TransactionsContract.Current.COLUMN_VALUE));
                    if (val != "") {
                        //Log.v("DBHelper: ", "val: " + val);
                        try {
                            first = Boolean.parseBoolean(val);
                        } catch (Exception e) {
                        }
                    }
                }
            }
            //database.close();
        }
        catch (Exception e) { Log.v(TAG, e.toString());}
        return first;
    }

    public void UpdateBank(Context context, String balance)
    {
        try {
            // Gets the data repository in write mode
            SQLiteDatabase db = new TransactionDbHelper(context).getWritableDatabase();

            // remove all previous entries
            String query = "DELETE FROM " + TransactionsContract.Current.TABLE_NAME +
                    " WHERE _id=1";
            db.execSQL(query);

            // now add the new value in
            ContentValues values = new ContentValues();
            values.put(TransactionsContract.Current._ID, 1);
            values.put(TransactionsContract.Current.COLUMN_VALUE, balance);
            db.replace(TransactionsContract.Current.TABLE_NAME,
                    null, values);
            //db.close();
        }
        catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    public float GetBankBalance(Context context)
    {
        // read in the values from the database
        float first = 0;
        try {
            SQLiteDatabase database = new TransactionDbHelper(context).getReadableDatabase();
            String query = "SELECT * FROM " + TransactionsContract.Current.TABLE_NAME + " WHERE " +
                    "_id=1";
            Cursor c = database.rawQuery(query, null);
            if (c != null) {
                while (c.moveToNext()) {
                    String val = c.getString(c.getColumnIndex(
                            TransactionsContract.Current.COLUMN_VALUE));
                    if (val != "") {
                        //Log.v("DBHelper: ", "val: " + val);
                        try {
                            first = Float.parseFloat(val);
                        } catch (Exception e) {
                        }
                    }
                }
            }
            //database.close();
        }
        catch (Exception e) { Log.v(TAG, e.toString());}
        return first;
    }

    public void ClearTransaction(Context context, int pos, boolean cleared)
    {
        try {
            SQLiteDatabase database = new TransactionDbHelper(context).getWritableDatabase();
            String idholder = "";

            // get the ID number
            int cnt = 0;

            String query = "SELECT * FROM " + TransactionsContract.TransactionEntry.TABLE_NAME;
            Cursor c = database.rawQuery(query, null);
            if (c != null) {
                while (c.moveToNext()) {
                    if (cnt == pos) {
                        idholder = c.getString(c.getColumnIndex(
                                TransactionsContract.TransactionEntry._ID));
                    }
                    cnt++;
                }
            }

            // decide to set high or low
            String clearVal = "0";
            if (cleared) clearVal = "1";

            // update query
            query = "UPDATE " + TransactionsContract.TransactionEntry.TABLE_NAME +
                    " SET " + TransactionsContract.TransactionEntry.HAS_CLEARED + " = " + clearVal +
                    " WHERE " + TransactionsContract.TransactionEntry._ID +
                    " = " + idholder;

            // now run the query
            database.execSQL(query);
            //database.close();
        }
        catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }
}

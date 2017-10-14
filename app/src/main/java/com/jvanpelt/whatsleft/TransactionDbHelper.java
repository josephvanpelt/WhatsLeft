package com.jvanpelt.whatsleft;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by blumojo on 10/1/17.
 */

public class TransactionDbHelper extends SQLiteOpenHelper {
    private String TAG = "TransactionDbHelper";
    //private static TransactionDbHelper mInstance = null;

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Transactions.db";
    private SQLiteDatabase database;
    private Context savedContext = null;

    // make this private so we don't accidentally leak - use get instance instead
    public TransactionDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        savedContext = context;
        //getInstance(context);
    }

    /*
    public static TransactionDbHelper getInstance(Context ctx) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (mInstance == null) {
            mInstance = new TransactionDbHelper(ctx.getApplicationContext());
        }
        return mInstance;
    }
    */

    private SQLiteDatabase getDBRead() {
        try {
            if (database == null) {
                database = new TransactionDbHelper(savedContext).getReadableDatabase();
            }
            return database;
        }
        catch (Exception e) {
            Log.e(TAG, e.toString());
            return null;
        }
    }

    private SQLiteDatabase getDBWrite() {
        try {
            if (database == null) {
                database = new TransactionDbHelper(savedContext).getWritableDatabase();
            }
            return database;
        }
        catch (Exception e) {
            Log.e(TAG, e.toString());
            return null;
        }
    }

    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(TransactionsContract.TransactionEntry.SQL_CREATE_ENTRIES);
            db.execSQL(TransactionsContract.Current.SQL_CREATE_ENTRIES);
        }
        catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            // not caching online data - so the below will not work
            //db.execSQL(TransactionsContract.TransactionEntry.SQL_DELETE_ENTRIES);
            //onCreate(db);
            // will need to Add ALTER commands etc.
        }
        catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    private void closeDB() {
        try {
            if (database != null) database.close();
            database = null;
        }
        catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    public ArrayList<ModelTrans> getAllTrans() {
        try {
            SQLiteDatabase database = getDBRead();
            String query = "SELECT * FROM " + TransactionsContract.TransactionEntry.TABLE_NAME;
            ArrayList<ModelTrans> transactions = new ArrayList<>();

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
            c.close();
            return transactions;
        }
        catch (Exception e) {
            Log.e(TAG, e.toString());
            return null;
        }
        finally {
            closeDB();
        }
    }

    public void DeleteTransaction(int pos)
    {
        try {
            SQLiteDatabase database = getDBWrite();
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
            c.close();
            // now delete it
            query = "DELETE FROM " + TransactionsContract.TransactionEntry.TABLE_NAME +
                    " WHERE _id = " + idholder + "";
            database.execSQL(query);
        }
        catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        finally {
            closeDB();
        }
    }

    public void UpdateTutorial(boolean Enable)
    {
        try {
            // Gets the data repository in write mode
            SQLiteDatabase database = getDBWrite();

            // remove all previous entries
            String query = "DELETE FROM " + TransactionsContract.Current.TABLE_NAME +
                    " WHERE _id=2";
            database.execSQL(query);

            // now add the new value in
            String val = Boolean.toString(Enable);
            ContentValues values = new ContentValues();
            values.put(TransactionsContract.Current._ID, 1);
            values.put(TransactionsContract.Current.COLUMN_VALUE, val);
            database.replace(TransactionsContract.Current.TABLE_NAME,
                    null, values);
        }
        catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        finally {
            closeDB();
        }
    }

    public boolean GetTutorialEnabled()
    {
        // read in the values from the database
        boolean first = true;
        try {
            SQLiteDatabase database = getDBRead();
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
            c.close();
        }
        catch (Exception e) { Log.v(TAG, e.toString());}
        finally {
            closeDB();
        }
        return first;
    }

    public void UpdateBank(String balance)
    {
        try {
            // Gets the data repository in write mode
            SQLiteDatabase db = getDBWrite();

            // remove all previous entries
            String query = "DELETE FROM " + TransactionsContract.Current.TABLE_NAME +
                    " WHERE _id=1";
            database.execSQL(query);

            // now add the new value in
            ContentValues values = new ContentValues();
            values.put(TransactionsContract.Current._ID, 1);
            values.put(TransactionsContract.Current.COLUMN_VALUE, balance);
            database.replace(TransactionsContract.Current.TABLE_NAME,
                    null, values);
        }
        catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        finally {
            closeDB();
        }
    }

    public float GetBankBalance()
    {
        // read in the values from the database
        float first = 0;
        try {
            SQLiteDatabase database = getDBRead();
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
            c.close();
        }
        catch (Exception e) { Log.v(TAG, e.toString());}
        finally {
            closeDB();
        }
        return first;
    }

    public void ClearTransaction(int pos, boolean cleared)
    {
        try {
            SQLiteDatabase database = getDBWrite();
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
            c.close();

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
        }
        catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        finally {
            closeDB();
        }
    }

    public boolean SaveToDB(String title, String value, long hasCleared)
    {
        try {
            // Gets the data repository in write mode
            SQLiteDatabase db = getDBWrite();

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(TransactionsContract.TransactionEntry.COLUMN_NAME, title);
            values.put(TransactionsContract.TransactionEntry.VALUE, value);
            values.put(TransactionsContract.TransactionEntry.HAS_CLEARED, hasCleared);

            // Insert the new row, returning the primary key value of the new row
            long newRowId = db.insert(TransactionsContract.TransactionEntry.TABLE_NAME,
                    null, values);

            return true;
        }
        catch (Exception e)
        {
            Log.e(TAG, "Error", e);
            return false;
        }
    }

    public void ChangeName(int pos, String val)
    {
        try {
            SQLiteDatabase database = getDBWrite();
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
            c.close();
            // now delete it
            query = "UPDATE " + TransactionsContract.TransactionEntry.TABLE_NAME +
                    " SET " + TransactionsContract.TransactionEntry.COLUMN_NAME +
                    " = '" + val + "'" +
                    " WHERE " + TransactionsContract.TransactionEntry._ID +
                    " = " + idholder;
            database.execSQL(query);
        }
        catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        finally {
            closeDB();
        }
    }

    public void ChangeValue(int pos, String val)
    {
        try {
            SQLiteDatabase database = getDBWrite();
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
            c.close();
            // now delete it
            query = "UPDATE " + TransactionsContract.TransactionEntry.TABLE_NAME +
                    " SET " + TransactionsContract.TransactionEntry.VALUE + " = " + val +
                    " WHERE " + TransactionsContract.TransactionEntry._ID +
                    " = " + idholder;
            database.execSQL(query);
        }
        catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        finally {
            closeDB();
        }
    }

}

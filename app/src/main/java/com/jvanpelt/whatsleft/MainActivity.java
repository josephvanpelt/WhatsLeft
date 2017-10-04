package com.jvanpelt.whatsleft;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";
    private InputActivity activity1 = new InputActivity();
    private EditActivity activity2 = new EditActivity();
    private ReviewActivity activity3 = new ReviewActivity();
    private WhatsLeftActivity activity4 = new WhatsLeftActivity();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_1:
                    //mTextMessage.setText(R.string.title_home);
                    //Toast.makeText(MainActivity.this, "You are already at this step.", 1).show();
                    switchFragment(1);
                    return true;
                case R.id.navigation_2:
                    //Toast.makeText(MainActivity.this, title_2, 1).show();
                    switchFragment(2);
                    return true;
                case R.id.navigation_3:
                    //Toast.makeText(MainActivity.this, title_3, 1).show();
                    switchFragment(3);
                    return true;
                case R.id.navigation_4:
                    //Toast.makeText(MainActivity.this, title_4, 1).show();
                    switchFragment(4);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        switchFragment(1);
    }

    private void switchFragment(int pos) {
        if (pos == 1) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_fragmentholder, activity1, "")
                    .commit();
        }
        else if (pos == 2)
        {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_fragmentholder, activity2, "")
                    .commit();
        }
        else if (pos == 3)
        {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_fragmentholder, activity3, "")
                    .commit();
        }
        else if (pos == 4)
        {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_fragmentholder, activity4, "")
                    .commit();
        }
    }

    /*
    public void SaveToDB(String title, long hasCleared, String reOccurance,
                         String startDate)
    {
        // Gets the data repository in write mode
        SQLiteDatabase db = TransactionDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(TransactionsContract.TransactionEntry.COLUMN_NAME, title);
        values.put(TransactionsContract.TransactionEntry.HAS_CLEARED, hasCleared);
        values.put(TransactionsContract.TransactionEntry.REOCCURANCE, reOccurance);

        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime((new SimpleDateFormat("dd/MM/yyyy")).parse(
                    binding.foundedEditText.getText().toString()));
            long date = calendar.getTimeInMillis();
            values.put(TransactionsContract.TransactionEntry.START_DATE, date);
        }
        catch (Exception e) {
            Log.e(TAG, "Error", e);
            Toast.makeText(this, "Date is in the wrong format", Toast.LENGTH_LONG).show();
            return;
        }

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(TransactionsContract.TransactionEntry.TABLE_NAME,
                null, values);

        Toast.makeText(this, "The new Row Id is " + newRowId, Toast.LENGTH_LONG).show();
    }

    private void readFromDB() {
        String name = binding.nameEditText.getText().toString();
        String desc = binding.descEditText.getText().toString();
        long date = 0;

        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime((new SimpleDateFormat("dd/MM/yyyy")).parse(
                    binding.foundedEditText.getText().toString()));
            date = calendar.getTimeInMillis();
        }
        catch (Exception e) {}

        SQLiteDatabase database = new TransactionDbHelper(this).getReadableDatabase();

        String[] projection = {
                TransactionsContract.TransactionEntry._ID,
                TransactionsContract.TransactionEntry.COLUMN_NAME,
                TransactionsContract.TransactionEntry.HAS_CLEARED,
                TransactionsContract.TransactionEntry.REOCCURANCE,
                TransactionsContract.TransactionEntry.START_DATE
        };

        String selection =
                TransactionsContract.TransactionEntry.COLUMN_NAME + " like ? and " +
                        TransactionsContract.TransactionEntry.START_DATE + " > ?";

        String[] selectionArgs = {"%" + name + "%", date + "", "%" + desc + "%"};

        Cursor cursor = database.query(
                TransactionsContract.TransactionEntry.TABLE_NAME,     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                      // don't sort
        );

        Log.d(TAG, "The total cursor count is " + cursor.getCount());
        binding.recycleView.setAdapter(new SampleRecyclerViewCursorAdapter(this, cursor));
    }
    */

}

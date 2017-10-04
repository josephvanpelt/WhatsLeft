package com.jvanpelt.whatsleft;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by blumojo on 10/1/17.
 */

public class EditActivity extends Fragment {
    private String TAG = "EditActivity";
    private TextView name;
    private TextView value;
    private CheckBox clear;
    private TransactionDbHelper dbHelper;
    private Button add;
    private Button next;
    private View view;
    private String gName = "";
    private String gValue = "";
    private long gCleared = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // get the view
        view = inflater.inflate(R.layout.activity_edit, container, false);

        try {
            dbHelper = new TransactionDbHelper(this.getContext());

            name = (TextView) view.findViewById(R.id.edtTransName);
            value = (TextView) view.findViewById(R.id.edtTransValue);
            clear = (CheckBox) view.findViewById(R.id.chkTransClear);
            add = (Button) view.findViewById(R.id.btnAddTrans);
            next = (Button) view.findViewById(R.id.btnNextEdit);

            // tie the buttons to functions
            add.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View v) {
                        onBtnAddClicked(v);
                    }
                });

            // tie the buttons to functions
            next.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v) {
                    // go to the next view
                    ReviewActivity activity3 = new ReviewActivity();
                    final FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.frame_fragmentholder, activity3, "");
                    ft.commit();
                }
            });

        }
        catch (Exception e) {
            Log.e(TAG, "Error", e);
        }

        return view;
    }

    public void onBtnAddClicked(View v){
        // get the values
        gName = name.getText().toString();
        gValue = value.getText().toString();
        boolean has_cleared = clear.isChecked();
        gCleared = 0;
        if (has_cleared) gCleared = 1;

        // check the values
        if (gName.equals("")) {
            Toast.makeText(this.getContext(), "Transactions must have a name", Toast.LENGTH_LONG).show();
            return;
        }
        if (gValue.equals("")) {
            Toast.makeText(this.getContext(), "Transactions must have a value", Toast.LENGTH_LONG).show();
            return;
        }

        // confirm deposit transaction
        float v1 = Float.parseFloat(gValue);
        if (v1 > 0)
        {
            String msg = "Are you sure you want to add this\n" +
                    "transaction as a deposit? (non-negative value)";
            new AlertDialog.Builder(view.getContext())
                    .setTitle("Delete?")
                    .setMessage(msg)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // they want this so add it
                            saveTrans();

                        }})
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // cancel out
                            return;
                        }
                    }).show();
        }
        else
        {
            saveTrans();
        }
    }

    public void saveTrans()
    {
        // pass the value to the Database
        if (SaveToDB(gName, gValue, gCleared))
        {
            // reset the values
            name.setText("");
            value.setText("");
            clear.setChecked(false);
        }
        else
        {
            Toast.makeText(view.getContext(), "Error adding transaction", Toast.LENGTH_LONG).show();
        }
    }

    public boolean SaveToDB(String title, String value, long hasCleared)
    {
        try {
            // Gets the data repository in write mode
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(TransactionsContract.TransactionEntry.COLUMN_NAME, title);
            values.put(TransactionsContract.TransactionEntry.VALUE, value);
            values.put(TransactionsContract.TransactionEntry.HAS_CLEARED, hasCleared);

            // Insert the new row, returning the primary key value of the new row
            long newRowId = db.insert(TransactionsContract.TransactionEntry.TABLE_NAME,
                    null, values);
            String msg = "The transaction was added.\n" +
                    "Add more or proceed to the next step";
            Toast.makeText(this.getContext(), msg, Toast.LENGTH_LONG).show();

            return true;
        }
        catch (Exception e)
        {
            Log.e(TAG, "Error", e);
            return false;
        }
    }

}

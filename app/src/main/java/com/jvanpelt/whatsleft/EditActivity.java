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
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
    private MainActivity main;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // get the view
        view = inflater.inflate(R.layout.activity_edit, container, false);

        try {
            main = (MainActivity) this.getActivity();
            dbHelper = new TransactionDbHelper(this.getContext());

            name = (TextView) view.findViewById(R.id.edtTransName);
            value = (TextView) view.findViewById(R.id.edtTransValue);
            clear = (CheckBox) view.findViewById(R.id.chkTransClear);
            add = (Button) view.findViewById(R.id.btnAddTrans);
            next = (Button) view.findViewById(R.id.btnNextEdit);

            Button prev = (Button) view.findViewById(R.id.btnPrev);
            // tie the buttons to functions
            prev.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v) {
                    // go to the next view
                    InputActivity activity = new InputActivity();
                    final FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.frame_fragmentholder, activity, "");
                    ft.commit();
                    main.UpdateView(1);
                }
            });

            Button help = (Button) view.findViewById(R.id.btnHelp);
            help.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v) {
                    main.displayTut(2);
                }
            });

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
                    ReviewActivity activity = new ReviewActivity();
                    final FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.frame_fragmentholder, activity, "");
                    ft.commit();
                    main.UpdateView(3);
                }
            });

            // add gesture support
            final GestureDetector gesture = new GestureDetector(getActivity(),
                    new GestureDetector.SimpleOnGestureListener() {

                        @Override
                        public boolean onDown(MotionEvent e) {
                            return true;
                        }

                        @Override
                        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                                               float velocityY) {
                            Log.i(TAG, "onFling has been called!");
                            final int SWIPE_MIN_DISTANCE = 120;
                            final int SWIPE_MAX_OFF_PATH = 250;
                            final int SWIPE_THRESHOLD_VELOCITY = 200;
                            try {
                                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                                    return false;
                                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                                    Log.i(TAG, "Right to Left");
                                    // go to the next view
                                    ReviewActivity activity3 = new ReviewActivity();
                                    final FragmentTransaction ft = getFragmentManager().beginTransaction();
                                    ft.replace(R.id.frame_fragmentholder, activity3, "");
                                    ft.commit();
                                    main.UpdateView(3);
                                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                                    Log.i(TAG, "Left to Right");
                                    // go to the previous view
                                    InputActivity activity1 = new InputActivity();
                                    final FragmentTransaction ft = getFragmentManager().beginTransaction();
                                    ft.replace(R.id.frame_fragmentholder, activity1, "");
                                    ft.commit();
                                    main.UpdateView(1);
                                }
                            } catch (Exception e) {
                                // nothing
                            }
                            return super.onFling(e1, e2, velocityX, velocityY);
                        }
                    });

            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return gesture.onTouchEvent(event);
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

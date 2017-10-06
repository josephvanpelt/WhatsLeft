package com.jvanpelt.whatsleft;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.widget.AdapterView.*;

/**
 * Created by blumojo on 10/1/17.
 */

public class ReviewActivity extends Fragment {

    private String TAG = "ReviewActivity";
    private TransactionDbHelper dbHelper;
    private Button delete;
    private Button mark_clear;
    private Button mark_un;
    private Button next;
    private GridView gv;
    private AdapterTrans adapter;
    private View gview;
    ArrayList<ModelTrans> transList = new ArrayList<>();
    private int gPosition = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        gview = inflater.inflate(R.layout.activity_review, container, false);

        try {
            dbHelper = new TransactionDbHelper(this.getContext());

            delete = (Button) gview.findViewById(R.id.btnDelete);
            mark_clear = (Button) gview.findViewById(R.id.btnMarkClear);
            mark_un = (Button) gview.findViewById(R.id.btnMarkUnClear);
            gv = (GridView) gview.findViewById(R.id.gvTrans);
            next = (Button) gview.findViewById(R.id.btnNextReview);

            transList = dbHelper.getAllTrans(this.getContext());
            adapter = new AdapterTrans(this.getContext(), transList);
            gv.setAdapter(adapter);

            gv.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //Log.v(TAG, "position: " + position);
                    gPosition = position;
                    //Toast.makeText(gview.getContext(), "position: " + position, Toast.LENGTH_LONG).show();
                }
            });

            // tie the buttons to functions
            delete.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v) {
                    ModelTrans md = transList.get(gPosition);
                    String msg = "Do you really want to delete:\n" + md.getName() +
                            " (" + md.getValue() + ")";
                    // confirm before delete the transaction selected
                    new AlertDialog.Builder(gview.getContext())
                            .setTitle("Delete?")
                            .setMessage(msg)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    // perform the delete
                                    dbHelper.DeleteTransaction(gview.getContext(), gPosition);

                                    // update the elements in the adapter
                                    adapter.clearAdapter();
                                    transList = dbHelper.getAllTrans(gview.getContext());
                                    adapter.addNewValues(transList);
                                    // notify for re-draw
                                    adapter.notifyDataSetChanged();
                                    gv.invalidateViews();

                                    Toast.makeText(gview.getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                                }})
                            .setNegativeButton(android.R.string.no, null).show();
                }
            });

            mark_clear.setOnClickListener(new View.OnClickListener(){
                  public void onClick(View v) {
                      dbHelper.ClearTransaction(gview.getContext(), gPosition, true);
                      // update the elements in the adapter
                      adapter.clearAdapter();
                      transList = dbHelper.getAllTrans(gview.getContext());
                      adapter.addNewValues(transList);
                      // notify for re-draw
                      adapter.notifyDataSetChanged();
                      gv.invalidateViews();
                  }
              });

            mark_un.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v) {
                    dbHelper.ClearTransaction(gview.getContext(), gPosition, false);
                    // update the elements in the adapter
                    adapter.clearAdapter();
                    transList = dbHelper.getAllTrans(gview.getContext());
                    adapter.addNewValues(transList);
                    // notify for re-draw
                    adapter.notifyDataSetChanged();
                    gv.invalidateViews();
                }
            });

            // tie the buttons to functions
            next.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v) {
                    // go to the next view
                    WhatsLeftActivity activity4 = new WhatsLeftActivity();
                    final FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.frame_fragmentholder, activity4, "");
                    ft.commit();
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
                                    WhatsLeftActivity activity3 = new WhatsLeftActivity();
                                    final FragmentTransaction ft = getFragmentManager().beginTransaction();
                                    ft.replace(R.id.frame_fragmentholder, activity3, "");
                                    ft.commit();
                                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                                    Log.i(TAG, "Left to Right");
                                    // go to the previous view
                                    EditActivity activity1 = new EditActivity();
                                    final FragmentTransaction ft = getFragmentManager().beginTransaction();
                                    ft.replace(R.id.frame_fragmentholder, activity1, "");
                                    ft.commit();
                                }
                            } catch (Exception e) {
                                // nothing
                            }
                            return super.onFling(e1, e2, velocityX, velocityY);
                        }
                    });

            gview.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return gesture.onTouchEvent(event);
                }
            });
        }
        catch (Exception e) {
            Log.e(TAG, "Error", e);
        }

        return gview;
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

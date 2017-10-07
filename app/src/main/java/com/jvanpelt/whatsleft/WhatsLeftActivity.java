package com.jvanpelt.whatsleft;

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
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by blumojo on 10/1/17.
 */

public class WhatsLeftActivity extends Fragment {

    private String TAG = "WhatsLeftActivity";
    private TextView calc;
    private TransactionDbHelper dbHelper;
    private View view;
    private TextView summary;
    private ImageView img;
    private MainActivity main;

    ArrayList<ModelTrans> transList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_whatsleft, container, false);
        Log.v(TAG, "loading ");
        try {
            main = (MainActivity) this.getActivity();
            dbHelper = new TransactionDbHelper(this.getContext());

            calc = (TextView) view.findViewById(R.id.txtCalculated);
            summary = (TextView) view.findViewById(R.id.txtSummary);
            img = (ImageView) view.findViewById(R.id.imgCoffee);

            Button prev = (Button) view.findViewById(R.id.btnPrev);
            // tie the buttons to functions
            prev.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v) {
                    // go to the next view
                    ReviewActivity activity = new ReviewActivity();
                    final FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.frame_fragmentholder, activity, "");
                    ft.commit();
                    main.UpdateView(3);
                }
            });

            String msgSum = "Initial Balance: $";

            // read in the values from the database
            float first = 0;
            SQLiteDatabase database = new TransactionDbHelper(this.getContext()).getReadableDatabase();
            String query = "SELECT * FROM " + TransactionsContract.Current.TABLE_NAME + " WHERE " +
                    "_id=1";
            Cursor c = database.rawQuery(query, null);
            if (c != null) {
                while (c.moveToNext()) {
                    String val = c.getString(c.getColumnIndex(
                            TransactionsContract.Current.COLUMN_VALUE));
                    if (val != "")
                    {
                        Log.v("DBHelper: ", "val: " + val);
                        try {
                            first = Float.parseFloat(val);
                        }
                        catch (Exception e) {}

                        msgSum += first + "\n\n" +
                                "Transactions to account for:\n" +
                                "\n";

                    }
                }
            }
            database.close();

            Log.v(TAG, "database closed");

            transList = dbHelper.getAllTrans(this.getContext());
            for (ModelTrans m : transList) {
                float v = m.getValue();
                if(!m.getCleared())  // if not cleared then count it
                {
                    first = first + v;
                    msgSum += " * " + m.getName() + ": $" + v + "\n";
                }
            }

            // get local currency format
            //NumberFormat format = NumberFormat.getCurrencyInstance(); //(Locale.getDefault());
            //String cl = format.format(Float.toString(first));

            String cl = "$" + String.format("%.2f",first);

            calc.setText(cl);
            summary.setText(msgSum);

            // update the picture based on the amount
            if (first > 0)
            {
                img.setImageResource(R.mipmap.ic_launcher);
            }
            else {
                img.setImageResource(R.mipmap.no_coffee);
            }

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

                                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                                    Log.i(TAG, "Left to Right");
                                    // go to the previous view
                                    ReviewActivity activity1 = new ReviewActivity();
                                    final FragmentTransaction ft = getFragmentManager().beginTransaction();
                                    ft.replace(R.id.frame_fragmentholder, activity1, "");
                                    ft.commit();
                                    main.UpdateView(3);
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
}

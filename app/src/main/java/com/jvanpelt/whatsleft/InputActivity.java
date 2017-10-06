package com.jvanpelt.whatsleft;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by blumojo on 10/1/17.
 */

public class InputActivity extends Fragment {
    private String TAG = "InputActivity";
    private TextView bank;
    private TransactionDbHelper dbHelper;
    private Button next;
    private TextView prev;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_input, container, false);

        try {
            dbHelper = new TransactionDbHelper(this.getContext());

            bank = (TextView) view.findViewById(R.id.edtCurrentBank);
            next = (Button) view.findViewById(R.id.btnNextInput);
            prev = (TextView) view.findViewById(R.id.txtLastValue);

            // grab the value any time it is changed
            bank.addTextChangedListener( new TextWatcher(){
                @Override
                public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}
                @Override
                public void onTextChanged (CharSequence s,
                                           int start,
                                           int before,
                                           int count) {
                    // get the string value
                    String val = bank.getText().toString();
                    // ignore blank string
                    if (val.equals("")) return;
                    // save the balance
                    SaveBankBalanceToDB(view.getContext(), val);
                    // update the summary on screen
                    updateBalance();
                    Log.i(TAG, "Balance change");
                }

                @Override public void afterTextChanged(Editable editable) {}
            });

            // tie the buttons to functions
            next.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v) {
                    // go to the next view
                    EditActivity activity2 = new EditActivity();
                    final FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.frame_fragmentholder, activity2, "");
                    ft.commit();
                }
            });

            updateBalance();

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
                                    EditActivity activity2 = new EditActivity();
                                    final FragmentTransaction ft = getFragmentManager().beginTransaction();
                                    ft.replace(R.id.frame_fragmentholder, activity2, "");
                                    ft.commit();
                                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                                    Log.i(TAG, "Left to Right");
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

    public void SaveBankBalanceToDB(Context context, String balance)
    {
        dbHelper.UpdateBank(context, balance);
    }

    public void updateBalance()
    {
        float prevval = dbHelper.GetBankBalance(view.getContext());
        String msg = "Current: $" + prevval;
        prev.setText(msg);
    }

}

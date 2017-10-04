package com.jvanpelt.whatsleft;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    ArrayList<ModelTrans> transList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_whatsleft, container, false);
        Log.v(TAG, "loading ");
        try {
            dbHelper = new TransactionDbHelper(this.getContext());

            calc = (TextView) view.findViewById(R.id.txtCalculated);
            summary = (TextView) view.findViewById(R.id.txtSummary);

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
                        msgSum += first + "\n-------------------------------------\n" +
                                "Transactions to account for:\n" +
                                "--------------------------------------\n";
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
        }
        catch (Exception e) {
            Log.e(TAG, "Error", e);
        }

        return view;
    }
}

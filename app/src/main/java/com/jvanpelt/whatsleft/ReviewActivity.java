package com.jvanpelt.whatsleft;

import android.content.DialogInterface;
import android.os.Bundle;
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
import android.widget.GridView;
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
    private MainActivity main;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        gview = inflater.inflate(R.layout.activity_review, container, false);

        try {
            main = (MainActivity) this.getActivity();
            //Toast.makeText(gview.getContext(), "position: " + main.currentView, Toast.LENGTH_LONG).show();

            dbHelper = new TransactionDbHelper(this.getContext());

            delete = (Button) gview.findViewById(R.id.btnDelete);
            mark_clear = (Button) gview.findViewById(R.id.btnMarkClear);
            mark_un = (Button) gview.findViewById(R.id.btnMarkUnClear);
            gv = (GridView) gview.findViewById(R.id.gvTrans);
            next = (Button) gview.findViewById(R.id.btnNextReview);

            Button prev = (Button) gview.findViewById(R.id.btnPrev);
            // tie the buttons to functions
            prev.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v) {
                    // go to the next view
                    EditActivity activity = new EditActivity();
                    final FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.frame_fragmentholder, activity, "");
                    ft.commit();
                    main.UpdateView(2);
                }
            });

            Button help = (Button) gview.findViewById(R.id.btnHelp);
            help.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v) {
                    main.displayTut(3);
                }
            });

            transList = dbHelper.getAllTrans();
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
                                    dbHelper.DeleteTransaction(gPosition);

                                    // update the elements in the adapter
                                    adapter.clearAdapter();
                                    transList = dbHelper.getAllTrans();
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
                      dbHelper.ClearTransaction(gPosition, true);
                      // update the elements in the adapter
                      adapter.clearAdapter();
                      transList = dbHelper.getAllTrans();
                      adapter.addNewValues(transList);
                      // notify for re-draw
                      adapter.notifyDataSetChanged();
                      gv.invalidateViews();
                  }
              });

            mark_un.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v) {
                    dbHelper.ClearTransaction(gPosition, false);
                    // update the elements in the adapter
                    adapter.clearAdapter();
                    transList = dbHelper.getAllTrans();
                    adapter.addNewValues(transList);
                    // notify for re-draw
                    adapter.notifyDataSetChanged();
                    gv.invalidateViews();
                }
            });

            // tie the buttons to functions
            next.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v) {
                    try {

                        // go to the next view
                        WhatsLeftActivity activity4 = new WhatsLeftActivity();
                        final FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.frame_fragmentholder, activity4, "");
                        ft.commit();

                        main.UpdateView(4);
                    }
                    catch (Exception e) {
                        Log.e(TAG, "Error", e);
                    }
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
                                    main.UpdateView(4);
                                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                                    Log.i(TAG, "Left to Right");
                                    // go to the previous view
                                    EditActivity activity1 = new EditActivity();
                                    final FragmentTransaction ft = getFragmentManager().beginTransaction();
                                    ft.replace(R.id.frame_fragmentholder, activity1, "");
                                    ft.commit();
                                    main.UpdateView(2);
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

    @Override
    public void onStop() {
        super.onStop();
        //dbHelper.close();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //dbHelper.close();
    }

}

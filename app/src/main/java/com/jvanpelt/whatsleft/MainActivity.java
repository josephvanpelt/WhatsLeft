package com.jvanpelt.whatsleft;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity
        implements TutorialInputFragment.OnTutorialInteractionListener{

    private final static String TAG = "MainActivity";
    private InputActivity activity1 = new InputActivity();
    private EditActivity activity2 = new EditActivity();
    private ReviewActivity activity3 = new ReviewActivity();
    private WhatsLeftActivity activity4 = new WhatsLeftActivity();
    public int currentView = 1;
    private static final String KEY_NAV = "navigationVal";
    SharedPreferences prefs = null;
    public TutorialInputFragment tut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences("com.jvanpelt.whatsleft", MODE_PRIVATE);

        tut = new TutorialInputFragment();

        //mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // check to see if the Activity was restarted when rotating, for instance
        if (savedInstanceState != null) {
            // see if a certain view state was saved when rotating
            int whichView = savedInstanceState.getInt(KEY_NAV);
            switchFragment(whichView);
        }
        else {
            switchFragment(1);
        }
    }

    // save the current view on rotation
    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_NAV, currentView);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (prefs.getBoolean("firstrun", true)) {
            // Do first run stuff here then set 'firstrun' as false
            TransactionDbHelper dbHelper = new TransactionDbHelper(this);
            dbHelper.UpdateTutorial(this, true); // enable the tutorial
            prefs.edit().putBoolean("firstrun", false).commit();
        }
    }

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

    public void UpdateView(int num) {
        currentView = num;
        BottomNavigationView bv = (BottomNavigationView) findViewById(R.id.navigation);

        //onNavigationItemSelected(bv.getMenu().getItem(0));
        switch (num) {
            case 1:
                switchFragment(1);
                bv.setSelectedItemId(bv.getMenu().getItem(0).getItemId());
                return;
            case 2:
                switchFragment(2);
                bv.setSelectedItemId(bv.getMenu().getItem(1).getItemId());
                return;
            case 3:
                switchFragment(3);
                bv.setSelectedItemId(bv.getMenu().getItem(2).getItemId());
                return;
            case 4:
                switchFragment(4);
                bv.setSelectedItemId(bv.getMenu().getItem(3).getItemId());
        }
    }

    private void switchFragment(int pos) {
        currentView = pos;
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

    public void onTutorialInteraction(){
        getSupportFragmentManager()
                .beginTransaction()
                .remove(tut)
                .commit();
    }

    public void displayTut(int num){
        tut = new TutorialInputFragment().newInstance(num);
        tut.ViewNum = num;
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.frame_fragmentholder, tut, "tut")
                .commit();
    }

}

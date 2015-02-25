package com.mentormate.academy.fbpartyapp;

import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.facebook.Session;
import com.facebook.widget.LoginButton;
import com.mentormate.academy.fbpartyapp.Fragments.AllEventsFragment;
import com.mentormate.academy.fbpartyapp.Fragments.TodayEventsFragment;
import com.mentormate.academy.fbpartyapp.Services.EventsDownloadService;
import com.mentormate.academy.fbpartyapp.Utils.Constants;

import java.util.Calendar;


public class Parties extends FragmentActivity implements View.OnClickListener {

    boolean isReceiverRegistered = false;
    private ActionBar actionBar;
    private PendingIntent pintent;
    private AlarmManager alarmManager;

    LoginButton fbAuthButton;

    ActionBar.Tab tabTodayEvents;
    ActionBar.Tab tabAllEvents;

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra(EventsDownloadService.KEY_MESSAGE)) {
                Log.d(Constants.LOG_DEBUG, "broadcast result: " + intent.getIntExtra(EventsDownloadService.KEY_MESSAGE, 0));

                //refresh fragment views
                todayEventsFragment.refresh(getApplicationContext());
                allEventsFragment.refresh(getApplicationContext());


            }
        }
    };

    TodayEventsFragment todayEventsFragment = new TodayEventsFragment();
    AllEventsFragment allEventsFragment = new AllEventsFragment();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //delete DB
        //int count = getContentResolver().delete(Constants.URI, null, null);
        //Log.d(Constants.LOG_DEBUG, "Deleted " + count + " events.");

        //start service manually
        /*Intent callingIntent = getIntent();
        if(callingIntent.hasExtra(Constants.INITIAL_STARTUP) &&
            callingIntent.getIntExtra(Constants.INITIAL_STARTUP, 0) == 1) {
            Log.d(Constants.LOG_DEBUG, "starting events download service");

            //download events info
            Intent eventsDownloadIntent = new Intent(this, EventsDownloadService.class);
            //eventsDownloadIntent.setAction(EventsDownloadService.ACTION_ASYNC);
            startService(eventsDownloadIntent);
        }*/

        //set Alarm -> call service every 1 hour
        Calendar cal = Calendar.getInstance();
        Intent intent = new Intent(Parties.this, EventsDownloadService.class);
        pintent = PendingIntent.getService(Parties.this, 0, intent, 0);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 60 * 60 * 1000, pintent);
        Log.d(Constants.LOG_DEBUG, "alarm: " + alarmManager.toString());

        //register receiver
        registerReceiver(receiver,
                new IntentFilter(EventsDownloadService.BROADCAST_RESULT));
        isReceiverRegistered = true;

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_events_main);
        setTabs();
    }

    private void setTabs() {

        actionBar = getActionBar();
        Log.d(Constants.LOG_DEBUG,"actionBar " + actionBar);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);


        tabTodayEvents = actionBar.newTab();
        tabTodayEvents.setText("Today");
        tabTodayEvents.setTabListener(new ActionBar.TabListener() {
            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
                fragmentTransaction.replace(R.id.tabsLayout, todayEventsFragment);
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

            }
        });

        actionBar.addTab(tabTodayEvents);

        tabAllEvents = actionBar.newTab();
        tabAllEvents.setText("Other Events");
        tabAllEvents.setTabListener(new ActionBar.TabListener() {
            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
                fragmentTransaction.replace(R.id.tabsLayout, allEventsFragment);
                Log.d(Constants.LOG_DEBUG, String.valueOf(todayEventsFragment.getmCurCheckPosition()));
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

            }
        });
        actionBar.addTab(tabAllEvents);
    }

    @Override
    protected void onResume() {
        super.onResume();

        LoginButton fbAuthButton = (LoginButton) findViewById(R.id.footerView);
        Log.d(Constants.LOG_DEBUG, "fbAuthButton: " + fbAuthButton.toString());
        //fbAuthButton = (LoginButton) includeView.findViewById(R.id.fbAuthButton);
        fbAuthButton.setOnClickListener(this);

        //fbAuthButton = (LoginButton) findViewById(R.id.fbAuthButton);

        //register receiver
        registerReceiver(receiver,
                new IntentFilter(EventsDownloadService.BROADCAST_RESULT));
        isReceiverRegistered = true;
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//
//        if(isReceiverRegistered && receiver!=null) {
//            unregisterReceiver(receiver);
//        }
//    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        if(isReceiverRegistered && receiver!=null) {
//            unregisterReceiver(receiver);
//        }
//    }


    @Override
    protected void onDestroy() {

        if(pintent != null && alarmManager != null)
        {
            Log.d(Constants.LOG_DEBUG, "cancelling alarm...");
            alarmManager.cancel(pintent);
        }

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_parties, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.footerView:
                callFacebookLogout(this);
                break;
        }
    }

    /**
     * Logout From Facebook
     */
    public void callFacebookLogout(Context context) {
        Session session = Session.getActiveSession();
        if (session != null) {

            if (!session.isClosed()) {
                session.closeAndClearTokenInformation();

                //clear your preferences if saved
            }
        } else {

            session = new Session(context);
            Session.setActiveSession(session);

            session.closeAndClearTokenInformation();


            //clear your preferences if saved

        }
        Log.d(Constants.LOG_DEBUG, "callFacebookLogout");
        Intent mainActivity = new Intent(this, MainActivity.class);
        startActivity(mainActivity);
    }
}

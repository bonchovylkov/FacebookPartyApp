package com.mentormate.academy.fbpartyapp;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.facebook.Session;
import com.mentormate.academy.fbpartyapp.Fragments.AllEventsFragment;
import com.mentormate.academy.fbpartyapp.Fragments.TodayEventsFragment;
import com.mentormate.academy.fbpartyapp.Services.EventsDownloadService;
import com.mentormate.academy.fbpartyapp.Utils.Constants;
import com.mentormate.academy.fbpartyapp.Utils.SingletonSession;


public class Parties extends FragmentActivity {

    private Session session;

    private ActionBar actionBar;

    ActionBar.Tab tabTodayEvents;
    ActionBar.Tab tabAllEvents;

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra(EventsDownloadService.KEY_MESSAGE)) {
                Log.d(Constants.LOG_DEBUG, "broadcast result: " + intent.getIntExtra(EventsDownloadService.KEY_MESSAGE, 0));

                todayEventsFragment.refresh(getApplicationContext());
                allEventsFragment.refresh(getApplicationContext());

                actionBar.setSelectedNavigationItem(1);
            }
        }
    };

    TodayEventsFragment todayEventsFragment = new TodayEventsFragment();
    AllEventsFragment allEventsFragment = new AllEventsFragment();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //check session
        session = SingletonSession.getInstance().getCurrentSession();
        if(session == null || !session.isOpened())
        {
            //we should be here only with an active session
            //Go back to main activity
            Intent mainIntent = new Intent(this, MainActivity.class);
            startActivity(mainIntent);
        }

        //delete DB
//        int count = getContentResolver().delete(Constants.URI, null, null);
//        Log.d(Constants.LOG_DEBUG, "Deleted " + count + " events.");

        //download events info
        Intent eventsDownloadIntent = new Intent(this, EventsDownloadService.class);
        eventsDownloadIntent.setAction(EventsDownloadService.ACTION_ASYNC);
        startService(eventsDownloadIntent);


        //register receiver
        registerReceiver(receiver,
                new IntentFilter(EventsDownloadService.BROADCAST_RESULT));

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_events_main);
        setTabs();

        /*if (savedInstanceState == null) {
            // Add the fragment on initial activity setup
            facebookLoginFragment = new FacebookLoginFragment(getBaseContext());
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, facebookLoginFragment)
                    .commit();

        } else {
            // Or set the fragment from restored state info
            facebookLoginFragment = (FacebookLoginFragment) getSupportFragmentManager()
                    .findFragmentById(android.R.id.content);
        }*/

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


                //  FrameLayout layout =(FrameLayout) findViewById( R.id.mainLayout);
                //  FrameLayout listCinemas = (FrameLayout)  layout.findViewById(R.id.cinemasLayout);

                //  listCinemas.setVisibility(View.VISIBLE);

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
    protected void onStop() {
        super.onStop();
        if(receiver!=null) {
            unregisterReceiver(receiver);
        }
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
}

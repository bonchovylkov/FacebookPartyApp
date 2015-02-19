package com.mentormate.academy.fbpartyapp;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.FrameLayout;

import com.mentormate.academy.fbpartyapp.Fragments.AllEventsFragment;
import com.mentormate.academy.fbpartyapp.Fragments.TodayEventsFragment;
import com.mentormate.academy.fbpartyapp.Services.EventsDownloadService;
import com.mentormate.academy.fbpartyapp.Services.PartiesDownloadService;
import com.mentormate.academy.fbpartyapp.Utils.Constants;


public class Parties extends Activity {

    ActionBar.Tab tabTodayEvents;
    ActionBar.Tab tabAllEvents;

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra(EventsDownloadService.KEY_MESSAGE)) {
                Log.d(Constants.LOG_DEBUG, "" + intent.getIntExtra(EventsDownloadService.KEY_MESSAGE, 0));

                FrameLayout allEvents = (FrameLayout) findViewById(R.id.tabsLayout);
                allEvents.invalidate();

            }
        }
    };

    TodayEventsFragment todayEventsFragment = new TodayEventsFragment();
    AllEventsFragment allEventsFragment = new AllEventsFragment();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

    }

    private void setTabs() {

        ActionBar actionBar = getActionBar();
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

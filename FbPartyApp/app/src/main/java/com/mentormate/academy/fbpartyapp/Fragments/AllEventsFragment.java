package com.mentormate.academy.fbpartyapp.Fragments;

import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.mentormate.academy.fbpartyapp.CustomAdapters.AllEventsAdapter;
import com.mentormate.academy.fbpartyapp.CustomAdapters.TodayEventsAdapter;
import com.mentormate.academy.fbpartyapp.EventDetails;
import com.mentormate.academy.fbpartyapp.Models.Event;
import com.mentormate.academy.fbpartyapp.Utils.Constants;



/**
 * Created by Bon on 12/29/2014.
 */

//it's connected to xml by class="com.homeassignment.cinemaapp.CinemaFragment"  from the xml file
public class AllEventsFragment extends ListFragment  {
    private ListAdapter listAdapter;

    public int getmCurCheckPosition() {
        return mCurCheckPosition;
    }

   // CinemaAdapter adapter = new CinemaAdapter(this.getActivity());

    int mCurCheckPosition = 0;

    // onActivityCreated() is called when the activity's onCreate() method has returned.
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        Log.d(Constants.LOG_DEBUG, "AllEventsFragment:onActivityCreated");


        listAdapter = new AllEventsAdapter(getActivity());
        // Populate list with all the cinemas that the adapted has
        setListAdapter(listAdapter);



        if (savedInstanceState != null) {
            // Restore last state for checked position.
            mCurCheckPosition = savedInstanceState.getInt("currentChoice", 0);
        }

        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);

    }





    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Log.d(Constants.LOG_DEBUG, "onSaveInstanceState");
        outState.putInt("currentChoice", mCurCheckPosition);
    }



    // If the user clicks on an item in the list then the onListItemClick() method is called.
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        mCurCheckPosition = position;

        //getListView().setItemChecked(mCurCheckPosition, true);
        setSelectedItemColor(l, v,null);

        Log.d(Constants.LOG_DEBUG, "onListItemClick position is" + position);

        AllEventsAdapter adapter = new AllEventsAdapter(this.getActivity());
        Event event= adapter.getItem(position);

        Intent intent = new Intent();
        intent.setClass(getActivity(), EventDetails.class);
        intent.putExtra(Constants.INTENT_EVENT_EXTRA_PARAM,event);
        startActivity(intent);
    }

    private void setSelectedItemColor(ListView l, View v,String index) {

        //remove color to all
        for(int a = 0; a < l.getChildCount(); a++)
        {
            l.getChildAt(a).setBackgroundColor(Color.TRANSPARENT);
        }

        //set color only to the currently selected item
        if (index==null){
            v.setBackgroundColor(Color.parseColor("#B2DAE8"));
        }else{
            l.getChildAt(Integer.parseInt(index)).setBackgroundColor(Color.parseColor("#B2DAE8"));
        }


    }


    public void refresh(Context context) {
        listAdapter = new AllEventsAdapter(context);
        this.setListAdapter(listAdapter);
    }

}

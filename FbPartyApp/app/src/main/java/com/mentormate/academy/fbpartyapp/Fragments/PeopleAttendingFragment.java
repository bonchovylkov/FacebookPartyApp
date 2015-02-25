package com.mentormate.academy.fbpartyapp.Fragments;

import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.mentormate.academy.fbpartyapp.CustomAdapters.AllEventsAdapter;
import com.mentormate.academy.fbpartyapp.CustomAdapters.PeopleAttendingAdapter;
import com.mentormate.academy.fbpartyapp.EventDetails;
import com.mentormate.academy.fbpartyapp.Models.PersonFB;
import com.mentormate.academy.fbpartyapp.Utils.Constants;

/**
 * Created by Bon on 2/25/2015.
 */
public class PeopleAttendingFragment extends ListFragment {
    private PeopleAttendingAdapter adapter;

    public PeopleAttendingFragment() {
    }

    public PeopleAttendingFragment(PeopleAttendingAdapter adapter) {
        this.adapter = adapter;
    }

    // onActivityCreated() is called when the activity's onCreate() method has returned.
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        Log.d(Constants.LOG_DEBUG, "PeopleAttendingFragment:onActivityCreated");
        setListAdapter(adapter);

        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);

    }





    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Log.d(Constants.LOG_DEBUG, "onSaveInstanceState");

    }



    // If the user clicks on an item in the list then the onListItemClick() method is called.
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        Log.d(Constants.LOG_DEBUG, "onListItemClick position is" + position);

        //eventually
       // PersonFB personFB= adapter.getItem(position);


      // Intent intent = new Intent();
      // intent.setClass(getActivity(), PersonDetails.class);
      // intent.putExtra(Constants.INTENT_EVENT_EXTRA_PARAM,personFB);
      // startActivity(intent);
    }




    public void refresh(PeopleAttendingAdapter adapter) {
        this.adapter = adapter;
        this.setListAdapter(this.adapter);
    }

}

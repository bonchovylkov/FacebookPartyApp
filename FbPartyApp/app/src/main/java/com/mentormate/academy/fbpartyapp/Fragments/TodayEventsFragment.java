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

import com.mentormate.academy.fbpartyapp.CustomAdapters.TodayEventsAdapter;
import com.mentormate.academy.fbpartyapp.EventDetails;
import com.mentormate.academy.fbpartyapp.Models.Event;
import com.mentormate.academy.fbpartyapp.Utils.Constants;



/**
 * Created by Bon on 12/29/2014.
 */
public class TodayEventsFragment extends ListFragment {

    private ListAdapter listAdapter;

    public int getSelectedCinemaIndex() {
        return selectedCinemaIndex;
    }

    public void setSelectedCinemaIndex(int selectedCinemaIndex) {
        this.selectedCinemaIndex = selectedCinemaIndex;
    }

    private  int selectedCinemaIndex = 0;

    public int getmCurCheckPosition() {
        return mCurCheckPosition;
    }

    int mCurCheckPosition = 0;

    // onActivityCreated() is called when the activity's onCreate() method has returned.
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        Log.d(Constants.LOG_DEBUG, "TitlesFragment:onActivityCreated");

        if (savedInstanceState != null) {
            // Restore last state for checked position.
            mCurCheckPosition = savedInstanceState.getInt("currentChoice", 0);
            selectedCinemaIndex = savedInstanceState.getInt("currentCinemaSelected",0);
        }

        //  setListAdapter(new MovieAdapter(this.getActivity(),selectedCinemaIndex));

        listAdapter = new TodayEventsAdapter(getActivity());
        if(listAdapter.getCount() > 0) {
            // Populate list with all the cinemas that the adapted has
            setListAdapter(listAdapter);
        }

        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        //this doesn't work for custom adapters
        // Highlight the selected item in the list view .

        //getListView().setItemChecked(mCurCheckPosition, true);

    }





    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Log.d(Constants.LOG_DEBUG, "onSaveInstanceState");
        outState.putInt("currentChoice", mCurCheckPosition);
        outState.putInt("currentCinemaSelected", selectedCinemaIndex);
    }

    // If the user clicks on an item in the list then the onListItemClick() method is called.
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        setSelectedItemColor(l, v,null);


        TodayEventsAdapter adapter = new TodayEventsAdapter(this.getActivity());
        Event event= adapter.getItem(position);

        Intent intent = new Intent();
        intent.setClass(getActivity(), EventDetails.class);
        intent.putExtra(Constants.INTENT_EVENT_EXTRA_PARAM,event);


        startActivity(intent);

    }

    private void setSelectedItemColor(ListView l, View v,String index) {
        for(int a = 0; a < l.getChildCount(); a++)
        {
            l.getChildAt(a).setBackgroundColor(Color.TRANSPARENT);
        }

        if (index==null){
            v.setBackgroundColor(Color.parseColor("#B2DAE8"));
        }else{
            l.getChildAt(Integer.parseInt(index)).setBackgroundColor(Color.parseColor("#B2DAE8"));
        }


    }


    public void refresh(Context context) {
        listAdapter = new TodayEventsAdapter(context);
        this.setListAdapter(listAdapter);
    }
}
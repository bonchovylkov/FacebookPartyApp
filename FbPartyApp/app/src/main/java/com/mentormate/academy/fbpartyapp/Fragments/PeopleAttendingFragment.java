package com.mentormate.academy.fbpartyapp.Fragments;

import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.mentormate.academy.fbpartyapp.CustomAdapters.AllEventsAdapter;
import com.mentormate.academy.fbpartyapp.CustomAdapters.PeopleAttendingAdapter;
import com.mentormate.academy.fbpartyapp.EventDetails;
import com.mentormate.academy.fbpartyapp.Models.PersonFB;
import com.mentormate.academy.fbpartyapp.Utils.Constants;

import java.util.List;

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

//        Intent browserIntent = new Intent(Intent.ACTION_VIEW,Uri.parse(
//                "http://www.facebook.com/sarmad.waleed.7"));
//        startActivity(browserIntent);

        Log.d(Constants.LOG_DEBUG, "onListItemClick position is" + position);

       PersonFB personFB= adapter.getItem(position);

        openFacebookProfile(personFB.getIdFb());

    }




    public void refresh(PeopleAttendingAdapter adapter) {
        this.adapter = adapter;
        this.setListAdapter(this.adapter);
    }


    public final void openFacebookProfile(String id) {
        final String urlFb = "fb://page/"+id;

        Intent intent = null;
        intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://facebook.com/" +id));
        this.startActivity(intent);


        //not working with the fb app
//        try {
//            // get the Facebook app if possible
//            this.getActivity().getPackageManager().getPackageInfo("com.facebook.katana", 0);
//            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlFb));// "fb://page/{id}"
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        } catch (Exception e) {
//            // no Facebook app, revert to browser
//
//        }


//        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlFb));
//
//        Log.d("clicked person id",id);
//        //intent.setData(Uri.parse(urlFb));
//
//        // If Facebook application is installed, use that else launch a browser
//        final PackageManager packageManager =getActivity(). getPackageManager();
//        List<ResolveInfo> list =
//                packageManager.queryIntentActivities(intent,
//                        PackageManager.MATCH_DEFAULT_ONLY);
//        if (list.size() == 0) {
//            final String urlBrowser = "https://www.facebook.com/"+id;
//            intent.setData(Uri.parse(urlBrowser));
//        }

      //  startActivity(intent);
    }

}

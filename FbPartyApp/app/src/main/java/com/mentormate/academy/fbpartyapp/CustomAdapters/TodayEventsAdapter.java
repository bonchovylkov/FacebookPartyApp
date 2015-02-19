package com.mentormate.academy.fbpartyapp.CustomAdapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.mentormate.academy.fbpartyapp.Models.Event;

/**
 * Created by Student11 on 2/16/2015.
 */
public class TodayEventsAdapter extends BaseAdapter {

    private Context context;
    public TodayEventsAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Event getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}

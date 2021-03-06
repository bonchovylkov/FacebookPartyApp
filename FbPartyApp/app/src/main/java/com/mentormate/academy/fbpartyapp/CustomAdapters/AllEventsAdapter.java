package com.mentormate.academy.fbpartyapp.CustomAdapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mentormate.academy.fbpartyapp.Models.Event;
import com.mentormate.academy.fbpartyapp.R;
import com.mentormate.academy.fbpartyapp.Utils.BaseHelper;
import com.mentormate.academy.fbpartyapp.Utils.Constants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Student11 on 2/16/2015.
 */
public class AllEventsAdapter extends BaseAdapter {

    private ArrayList<Event> list = new ArrayList<Event>();
    private Context context;
    public AllEventsAdapter(Context context) {

        this.context = context;
        Cursor c =  context.getContentResolver().query(Constants.URI, null, null, null, "");
        String result = "Results:";
        if (!c.moveToFirst()) {

             //Toast.makeText(context, result + " no content yet!", Toast.LENGTH_LONG).show();
        } else {
            do{
                Event event = BaseHelper.setEventData(c);
                list.add(event);


            } while (c.moveToNext());
            //Toast.makeText(this, result, Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Event getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView eventPicture;
        TextView eventName;
        TextView startDate;
        TextView endDate;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.event_row, parent, false);
            eventName = (TextView) convertView.findViewById(R.id.eventName);
            eventPicture = (ImageView) convertView.findViewById(R.id.partyPicture);
            startDate =  (TextView) convertView.findViewById(R.id.lbStartTime);

            //it works faster with set tag
            convertView.setTag(R.id.eventName, eventName);
            convertView.setTag(R.id.partyPicture, eventPicture);
            convertView.setTag(R.id.lbStartTime, startDate);



        } else {
            eventName = (TextView) convertView.getTag(R.id.eventName);
            eventPicture = (ImageView) convertView.getTag(R.id.partyPicture);
            startDate = (TextView) convertView.getTag(R.id.lbStartTime);

        }

        Event e = getItem(position);

        eventName.setText("" + BaseHelper.getSubstringByCount(e.getName(),100  ));
        startDate.setText("" +BaseHelper.getDateInFormat("", e.getStartTime()));
        Picasso.with(context).load(e.getCoverSource()).into(eventPicture);

        return convertView;
    }
}

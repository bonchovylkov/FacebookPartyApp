package com.mentormate.academy.fbpartyapp.CustomAdapters;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mentormate.academy.fbpartyapp.Models.Event;
import com.mentormate.academy.fbpartyapp.R;
import com.mentormate.academy.fbpartyapp.Utils.BaseHelper;
import com.mentormate.academy.fbpartyapp.Utils.Constants;

import java.util.ArrayList;

/**
 * Created by Student11 on 2/16/2015.
 */
public class TodayEventsAdapter extends BaseAdapter {

    private ArrayList<Event> list = new ArrayList<Event>();
    private Context context;
    public TodayEventsAdapter(Context context) {

        this.context = context;
        Cursor c =  context.getContentResolver().query(Uri.withAppendedPath(Constants.URI, "events_today"), null, null, null, "");
        String result = "Results:";
        if (!c.moveToFirst()) {

            Toast.makeText(context, result + " no content yet!", Toast.LENGTH_LONG).show();
        } else {
            do{
                String name = c.getString(c.getColumnIndex(Constants.DB_NAME));
                String id = c.getString(c.getColumnIndex(Constants.DB_ID));
                String startTime = c.getString(c.getColumnIndex(Constants.DB_START_TIME));

                Event event = new Event();
                event.setId((Integer.parseInt(id)));
                event.setName(name);
                event.setStartTime(startTime);

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
            eventPicture = (ImageView) convertView.findViewById(R.id.eventPicture);
            startDate =  (TextView) convertView.findViewById(R.id.lbStartDate);

            //it works faster with set tag
            convertView.setTag(R.id.eventName, eventName);
            convertView.setTag(R.id.eventPicture, eventPicture);
            convertView.setTag(R.id.lbStartDate, startDate);



        } else {
            eventName = (TextView) convertView.getTag(R.id.eventName);
            eventPicture = (ImageView) convertView.getTag(R.id.eventPicture);
            startDate = (TextView) convertView.getTag(R.id.lbStartDate);

        }

        eventName.setText("" + BaseHelper.getSubstringByCount(getItem(position).getName(), 100));
        startDate.setText("" +BaseHelper.getDateInFormat("", getItem(position).getStartTime()));
        //eventPicture.setBackgroundResource(getItem(position).getPicture());

        return convertView;
    }
}

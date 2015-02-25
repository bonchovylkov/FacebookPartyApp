package com.mentormate.academy.fbpartyapp.CustomAdapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mentormate.academy.fbpartyapp.Models.PersonFB;
import com.mentormate.academy.fbpartyapp.R;
import com.mentormate.academy.fbpartyapp.Utils.BaseHelper;
import com.mentormate.academy.fbpartyapp.Utils.Constants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Bon on 2/25/2015.
 */
public class PeopleAttendingAdapter extends BaseAdapter {


    private ArrayList<PersonFB> list = new ArrayList<PersonFB>();
    private Context context;
    public PeopleAttendingAdapter(Context context,ArrayList<PersonFB> listPeople) {

        this.context = context;
       this.list = listPeople;
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public PersonFB getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView personPicture;
        TextView personName;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.person_row, parent, false);

            personName = (TextView) convertView.findViewById(R.id.lbPersonName);
            personPicture = (ImageView) convertView.findViewById(R.id.personPictureImg);

            //it works faster with set tag
            convertView.setTag(R.id.lbPersonName, personName);
            convertView.setTag(R.id.personPictureImg, personPicture);



        } else {
            personName = (TextView) convertView.getTag(R.id.lbPersonName);
            personPicture = (ImageView) convertView.getTag(R.id.personPictureImg);

        }

        PersonFB p = getItem(position);

        personName.setText("" + BaseHelper.getSubstringByCount(p.getName(), 100));

        String userPictureURL = Constants.FB_PROFILE_PICTURE_FIRST_PART  + p.getIdFb()
                +Constants.FB_PROFILE_PICTURE_SECOND_PART;
        Log.d("person picture url:" , userPictureURL);
        Picasso.with(context).load(userPictureURL).into(personPicture);

        return convertView;
    }

}
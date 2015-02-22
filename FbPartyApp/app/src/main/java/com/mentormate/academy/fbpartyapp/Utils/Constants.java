package com.mentormate.academy.fbpartyapp.Utils;

import android.net.Uri;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by Bon on 2/15/2015.
 */
public class Constants {
    public static final String LOG_DEBUG= "LOG_DEBUG";
    public static final String FB_PAGE_WHERE_IS_THE_PARTY_ID_FEED = "341486209377198/feed";

    public static final String FB_EVENT_FIELDS_PARAM = "fields";
    public static final String FB_EVENT_FIELDS_LIST = "name,description,start_time,venue,cover";
    public static final String FB_FEED_SINCE_PARAM = "since";
    public static final String INTENT_EVENT_EXTRA_PARAM = "EVENT";

    public static final String EVENT_URI_SEGMENT = "events";

    public static final String AUTHORITY    = "com.mentormate.academy.fbpartyapp.Events.EventsProvider";
    public static final String DB_DBNAME    = "Events.db";
    public static final String URL          = "content://" + AUTHORITY + "/" + DB_DBNAME;
    public static final Uri URI             = Uri.parse(URL);

    public final static int DB_VERSION      = 4;
    public static final String TABLE_NAME = "Events";

    public static final String DB_ID        = "_id";
    public static final String DB_EVENT_ID  = "event_id";
    public static final String DB_URL       = "url";
    public static final String DB_START_TIME= "start_time";
    public static final String DB_NAME      = "name";
    public static final String DB_LAT       = "latitude";
    public static final String DB_LON       = "longitude";
    public static final String DB_COVER     = "cover";
    public static final String DB_DESCRIPTION      = "description";




    public static DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

}

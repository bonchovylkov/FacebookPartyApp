package com.mentormate.academy.fbpartyapp.Utils;

import android.net.Uri;

/**
 * Created by Bon on 2/15/2015.
 */
public class Constants {
    public static final String LOG_DEBUG= "LOG_DEBUG";
    public static final String FB_PAGE_WHERE_IS_THE_PARTY_ID_FEED = "341486209377198/feed";

    public static final String EVENT_URI_SEGMENT = "events";

    public static final String AUTHORITY    = "com.mentormate.academy.fbpartyapp.Events.EventsProvider";
    public static final String DB_DBNAME    = "Events.db";
    public static final String URL          = "content://" + AUTHORITY + "/" + DB_DBNAME;
    public static final Uri URI             = Uri.parse(URL);

    public final static int DB_VERSION      = 1;
    public static final String TABLE_NAME = "Events";

    public static final String DB_ID        = "_id";
    public static final String DB_EVENT_ID  = "event_id";
    public static final String DB_URL       = "url";
}

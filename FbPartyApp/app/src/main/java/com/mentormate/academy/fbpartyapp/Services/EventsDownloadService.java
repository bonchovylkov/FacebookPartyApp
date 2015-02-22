package com.mentormate.academy.fbpartyapp.Services;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.util.Patterns;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;
import com.mentormate.academy.fbpartyapp.Utils.Constants;
import com.mentormate.academy.fbpartyapp.Utils.SingletonSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Student11 on 2/19/2015.
 */
public class EventsDownloadService extends Service {

    public final static String ACTION_ASYNC = "com.mentormate.academy.fbpartyapp.Services.ACTION_ASYNC";
    public final static String BROADCAST_RESULT = "com.mentormate.academy.fbpartyapp.Services.BROADCAST_RESULT";
    public final static String KEY_MESSAGE = "events_downloaded";

    private Pattern pattern = Pattern.compile("^https?://[^/]+/([^/]+)/([^/]+)/.*");
    private Session session;
    private int eventsFound = 0;
    private long lastUpdate = 0;
    private SharedPreferences sharedPreferences;
    private String segmentOne, eventID;

    //private DateFormat formatter = new SimpleDateFormat("yyyy-MM-ddThh:mm:ssZ");
    private String datePattern = "yyyy-MM-dd'T'HH:mm:ssZ";


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sharedPreferences = getSharedPreferences("SETTINGS", Context.MODE_PRIVATE);
        if(sharedPreferences.contains("last_update")) {
            lastUpdate = sharedPreferences.getLong("last_update", lastUpdate);
        }
        Log.d(Constants.LOG_DEBUG, "lastUpdate:" + lastUpdate);

        session = SingletonSession.getInstance().getCurrentSession();
        if(session == null || !session.isOpened())
        {
            //we should be here only with an active session
           //TODO KILL service
        }

        Log.d(Constants.LOG_DEBUG, "start getRequestData");

        getRequestData(Constants.FB_PAGE_WHERE_IS_THE_PARTY_ID_FEED);

        Log.d(Constants.LOG_DEBUG, "after getRequestData");

        return super.onStartCommand(intent, flags, startId);
    }



    private void getRequestData(final String inRequestId) {
        //set active session to our session!
        Log.d(Constants.LOG_DEBUG, "currentSession: " + session);

        //params -> ?since=<unix timestamp of last update>
        Bundle params = new Bundle();
        //TO DO -> remove after testing
       // lastUpdate = 0;
        if(lastUpdate != 0) {
            params.putString(Constants.FB_FEED_SINCE_PARAM, "" + lastUpdate);
        }

        Log.d(Constants.LOG_DEBUG, "last_update: " + lastUpdate);

        // Create a new request for an HTTP GET with the
        // request ID as the Graph path.
        Request request = new Request(session,
                inRequestId, params, HttpMethod.GET, new Request.Callback() {

            @Override
            public void onCompleted(Response response) {
                // Process the returned response
                GraphObject graphObject = response.getGraphObject();

                FacebookRequestError error = response.getError();

                // Default message
                String message = "getRequestData onCompleted!";
                if (graphObject != null) {

                    JSONObject graphResult = graphObject.getInnerJSONObject();

                    parseFeedJson(graphResult.toString());

                    //Log.d(Constants.LOG_DEBUG, graphResult.toString());
                    setLastUpdateTime();

                    Intent intent = new Intent(BROADCAST_RESULT);
                    intent.putExtra(KEY_MESSAGE, eventsFound);
                    sendBroadcast(intent);

                } else if (error != null) {
                    Log.d(Constants.LOG_DEBUG, "graphObject is null!");

                }
            }
        });
        // Execute the request asynchronously.
        Request.executeBatchAsync(request);

        Log.d(Constants.LOG_DEBUG, "Request.executeBatchAsync(request): " + Request.executeBatchAsync(request).toString());
    }

    private void parseFeedJson(String jsonString) {
        /*
        //delete DB (testing purposes)
        int count = getContentResolver().delete(Constants.URI, null, null);
        Log.d(Constants.LOG_DEBUG, "Deleted " + count + " events.");*/

        Log.d(Constants.LOG_DEBUG, "jsonString: " + jsonString);

        if (jsonString.equals("")) return;

        //initial string debug
        //Log.d(Constants.LOG_DEBUG, "jsonString: " + jsonString);

        JSONObject feed, post, comments, comment;
        JSONArray data, commentsData;
        try {
            feed = new JSONObject(jsonString);

            data = feed.getJSONArray("data");

            for (int i = 0; i < data.length(); i++) {
                post = data.getJSONObject(i);

                //post debug
                //Log.d(Constants.LOG_DEBUG, "post: " + post.toString());

                if ( post.has("comments") ) {

                    comments = post.getJSONObject("comments");
                    commentsData = comments.getJSONArray("data");

                    for (int j = 0; j < commentsData.length(); j++) {

                        comment = commentsData.getJSONObject(j);
                        //check comment for event link
                        if ( checkCommentForEventLink(comment.getString("message")) ) {
                            eventsFound++;
                        }

                        //comment debug
                        //Log.d(Constants.LOG_DEBUG, "comment: " + comment.getString("message"));

                    }

                }
            }



        } catch (JSONException e) {

            Log.d(Constants.LOG_DEBUG, "Error....");

            e.printStackTrace();
        }

        Log.d(Constants.LOG_DEBUG, "Total events found: " + eventsFound);

    }

    private boolean checkCommentForEventLink(String message) {
        //valid url
        if (Patterns.WEB_URL.matcher(message).matches() ) {
            //look for facebook.com/events
            Matcher matcher = pattern.matcher(message);
            if( matcher.matches() )
            {
                //get first part of path -> should be equal to "events"
                segmentOne = matcher.group(1);
                if ( segmentOne.equals(Constants.EVENT_URI_SEGMENT) )
                {
                    //if the first part is events -> the second is eventID!
                    eventID = matcher.group(2);

                    //Log.d(Constants.LOG_DEBUG, "Message: " + message + " segmentOne: " + segmentOne + " eventID: " + eventID);

                    //save in DB

                    Cursor c = getContentResolver().query(Uri.withAppendedPath(Constants.URI, "event/" + eventID), null, null, null, "");
                    if( !c.moveToFirst() )
                    {
                        //event is not found in DB -> insert
                        ContentValues values = new ContentValues();
                        values.put(Constants.DB_EVENT_ID, eventID);
                        values.put(Constants.DB_URL, message);

                        //Log.d(Constants.LOG_DEBUG, "addEvent values: " + values.toString());
                        Uri uri = getContentResolver().insert(Constants.URI, values);
                        //Log.d(Constants.LOG_DEBUG, "addEvent uri: " + uri.toString());

                        updateEventData(eventID);
                    }

                    return true;
                }
            }
        }

        return false;
    }

    private void updateEventData(final String eventID) {

        Bundle params = new Bundle();
        params.putString(Constants.FB_EVENT_FIELDS_PARAM, Constants.FB_EVENT_FIELDS_LIST);

        Request request = new Request(session,
                eventID , params, HttpMethod.GET, new Request.Callback() {

            @Override
            public void onCompleted(Response response) {
                // Process the returned response
                GraphObject graphObject = response.getGraphObject();

                FacebookRequestError error = response.getError();

                // Default message
                //String message = "Incoming request";

                if (graphObject != null) {

                    JSONObject graphResult = graphObject.getInnerJSONObject();

                    Log.d(Constants.LOG_DEBUG, graphResult.toString());

                    ContentValues values = new ContentValues();
                    if( graphResult.has("name")) {
                        try {
                            values.put(Constants.DB_NAME, graphResult.getString("name"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    if( graphResult.has("description")) {
                        try {
                            values.put(Constants.DB_DESCRIPTION, graphResult.getString("description"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    if( graphResult.has("start_time")) {
                        try {
                            String start_time = graphResult.getString("start_time");
                            Date date = new SimpleDateFormat(datePattern).parse(start_time);
                            values.put(Constants.DB_START_TIME, Constants.formatter.format(date) );
                            //Log.d(Constants.LOG_DEBUG, "start_time: " + formatter.format(date));
                        } catch (JSONException e) {
                            //Log.d(Constants.LOG_DEBUG, "start_time json error " );
                            e.printStackTrace();
                        } catch (ParseException e) {
                            //Log.d(Constants.LOG_DEBUG, "start_time parse error " );
                            e.printStackTrace();
                        }
                    }

                    if( graphResult.has("venue")) {
                        try {
                            JSONObject venue = graphResult.getJSONObject("venue");

                            if( venue.has("latitude") && venue.has("longitude"))
                            {
                                values.put(Constants.DB_LAT, venue.getString("latitude"));
                                values.put(Constants.DB_LON, venue.getString("longitude"));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    if( graphResult.has("cover")) {
                        try {
                            JSONObject cover = graphResult.getJSONObject("cover");

                            if( cover.has("id") )
                            {
                                values.put(Constants.DB_COVER, cover.getString("id"));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    //Log.d(Constants.LOG_DEBUG, "Update values: " + values.toString());
                    int count = getContentResolver().update(Uri.withAppendedPath(Constants.URI, "event/" + eventID), values, null, null);
                    //Log.d(Constants.LOG_DEBUG, "Updated count: " + count);

                } else if (error != null) {
                    Log.d(Constants.LOG_DEBUG, "Error getting event " + eventID + " info!");
                }
            }
        });

        // Execute the request asynchronously.
        Request.executeBatchAsync(request);

    }

    private void setLastUpdateTime() {
        //update shared pref last update time
        long unixTime = System.currentTimeMillis() / 1000L;
        Log.d(Constants.LOG_DEBUG, "unixTime:" + unixTime);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("last_update", unixTime);
        editor.commit();
    }

}

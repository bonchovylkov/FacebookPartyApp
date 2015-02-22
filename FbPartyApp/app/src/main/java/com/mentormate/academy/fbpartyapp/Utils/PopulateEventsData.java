package com.mentormate.academy.fbpartyapp.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;
import com.mentormate.academy.fbpartyapp.Services.EventsDownloadService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ivaylokostov on 2/21/15.
 */
public class PopulateEventsData extends AsyncTask<String, Void, String> {

    private Context context;
    private Pattern pattern = Pattern.compile("^https?://[^/]+/([^/]+)/([^/]+)/.*");
    //private DateFormat formatter = new SimpleDateFormat("yyyy-MM-ddThh:mm:ssZ");
    private String datePattern = "yyyy-MM-dd'T'HH:mm:ssZ";
    private Session session;
    private String segmentOne, eventID;
    private int eventsFound = 0;

    public PopulateEventsData(Context context)
    {
        this.context = context;
        session = SingletonSession.getInstance().getCurrentSession();
    }


    @Override
    protected String doInBackground(String... params) {

        String jsonString = params[0];
        parseFeedJson(jsonString);

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.d(Constants.LOG_DEBUG, "populate events data onPostExecute");

        //send broadcast
        Intent intent = new Intent(EventsDownloadService.BROADCAST_RESULT);
        intent.putExtra(EventsDownloadService.KEY_MESSAGE, eventsFound);
        context.sendBroadcast(intent);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    private void parseFeedJson(String jsonString) {

        if (jsonString.equals(""))
        {
            Log.d(Constants.LOG_DEBUG, "empty jsonString");
            return;
        }

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

                    Cursor c = context.getContentResolver().query(Uri.withAppendedPath(Constants.URI, "event/" + eventID), null, null, null, "");
                    if( !c.moveToFirst() )
                    {
                        //event is not found in DB -> insert
                        ContentValues values = new ContentValues();
                        values.put(Constants.DB_EVENT_ID, eventID);
                        values.put(Constants.DB_URL, message);

                        //Log.d(Constants.LOG_DEBUG, "addEvent values: " + values.toString());
                        Uri uri = context.getContentResolver().insert(Constants.URI, values);
                        //Log.d(Constants.LOG_DEBUG, "addEvent uri: " + uri.toString());

                    }
                    else
                    {
                        Log.d(Constants.LOG_DEBUG, "event already in db : " + c.getString(c.getColumnIndex(Constants.DB_EVENT_ID)) + " " +  c.getString(c.getColumnIndex(Constants.DB_START_TIME)));
                    }
                    updateEventData(eventID);

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

                Log.d(Constants.LOG_DEBUG, "response: " + response);
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
                            Log.d(Constants.LOG_DEBUG, "start_time: " + Constants.formatter.format(date));
                        } catch (JSONException e) {
                            Log.d(Constants.LOG_DEBUG, "start_time json error " );
                            e.printStackTrace();
                        } catch (ParseException e) {
                            Log.d(Constants.LOG_DEBUG, "start_time parse error " );
                            e.printStackTrace();
                        }
                    }

                    if( graphResult.has("venue")) {
                        try {
                            JSONObject venue = graphResult.getJSONObject("venue");

                            if( venue.has("latitude") && venue.has("longitude"))
                            {
                                values.put(Constants.DB_LAT, venue.getDouble("latitude"));
                                values.put(Constants.DB_LON, venue.getDouble("longitude"));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    if( graphResult.has("cover")) {
                        try {
                            JSONObject cover = graphResult.getJSONObject("cover");

                            if( cover.has("source") )
                            {
                                values.put(Constants.DB_COVER, cover.getString("source"));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    Log.d(Constants.LOG_DEBUG, "Update values: " + values.toString());
                    int count = context.getContentResolver().update(Uri.withAppendedPath(Constants.URI, "event/" + eventID), values, null, null);
                    //Log.d(Constants.LOG_DEBUG, "Updated count: " + count);

                } else if (error != null) {
                    Log.d(Constants.LOG_DEBUG, "Error getting event " + eventID + " info!");
                }
            }
        });

        Log.d(Constants.LOG_DEBUG, "request: " + request);

        // Execute the request asynchronously.
        Request.executeAndWait(request);

    }
}

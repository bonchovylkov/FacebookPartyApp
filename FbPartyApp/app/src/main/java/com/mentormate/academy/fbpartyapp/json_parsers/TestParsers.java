package com.mentormate.academy.fbpartyapp.json_parsers;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;
import com.mentormate.academy.fbpartyapp.MainActivity;
import com.mentormate.academy.fbpartyapp.R;
import com.mentormate.academy.fbpartyapp.Utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestParsers extends Activity {

    private Pattern pattern = Pattern.compile("^https?://[^/]+/([^/]+)/([^/]+)/.*");
    private String segmentOne, eventID;

    Session session;

    private int eventsFound = 0;
    private long lastUpdate = 0;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_parsers);

        sharedPreferences = getSharedPreferences("SETTINGS", Context.MODE_PRIVATE);
        if(sharedPreferences.contains("last_update")) {
            lastUpdate = sharedPreferences.getLong("last_update", lastUpdate);
        }
        Log.d(Constants.LOG_DEBUG, "lastUpdate:" + lastUpdate);

        //DELETE THIS TO ENABLE lastUpdate functionality: get only posts after last download;
        //lastUpdate = 0;
        //Log.d(Constants.LOG_DEBUG, "lastUpdate (forced to 0):" + lastUpdate);

        //parseFeedJson("");

        Intent intent = getIntent();
        if( intent != null && intent.hasExtra("session") )
        {
            session = (Session) intent.getSerializableExtra("session");
        }

        if(session == null || !session.isOpened())
        {
            //we should be here only with an active session
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        }

        getRequestData(Constants.FB_PAGE_WHERE_IS_THE_PARTY_ID_FEED);

        Cursor c = getContentResolver().query(Constants.URI, null, null, null, "");
        //Cursor c = getContentResolver().query(Uri.withAppendedPath(Constants.URI, "33"), null, null, null, "");
        //Cursor c = getContentResolver().query(Uri.withAppendedPath(Constants.URI, "event/1562382110671319"), null, null, null, "");

        String result = "";
        if (!c.moveToFirst()) {
            Toast.makeText(this, result + " no content yet!", Toast.LENGTH_LONG).show();
        } else {
            do{
                result =
                        " id " +  c.getString(c.getColumnIndex(Constants.DB_ID)) +
                        " has event id: " + c.getString(c.getColumnIndex(Constants.DB_EVENT_ID)) +
                        " has url: " + c.getString(c.getColumnIndex(Constants.DB_URL));
                Log.d(Constants.LOG_DEBUG, result);
            } while (c.moveToNext());

        }
    }

    private void getRequestData(final String inRequestId) {
        //set active session to our session!
        Log.d(Constants.LOG_DEBUG, "currentSession: " + session);

        //params -> ?since=<unix timestamp of last update>
        Bundle params = new Bundle();
        params.putString(Constants.FB_FEED_SINCE_PARAM, "" + lastUpdate);

        //update shared pref last update time
        long unixTime = System.currentTimeMillis() / 1000L;
        Log.d(Constants.LOG_DEBUG, "unixTime:" + unixTime);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("last_update", unixTime);
        editor.commit();
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

                    Log.d(Constants.LOG_DEBUG, graphResult.toString());

                } else if (error != null) {
                    Log.d(Constants.LOG_DEBUG, "graphObject is null!");

                }
            }
        });
        // Execute the request asynchronously.
        Request.executeBatchAsync(request);

        //Log.d(Constants.LOG_DEBUG, "Request.executeBatchAsync(request): " + Request.executeBatchAsync(request).toString());
    }

    private void parseFeedJson(String jsonString) {
        //delete DB (testing purposes)

        int count = getContentResolver().delete(Constants.URI, null, null);
        Log.d(Constants.LOG_DEBUG, "Deleted " + count + " events.");

        Log.d(Constants.LOG_DEBUG, "jsonString: " + jsonString);

        //LOCAL JSON PARSING TEST
        InputStream inputStream;
        if(jsonString.equals("")) {
            try {
                inputStream = getAssets().open("sample_feed.json");
                int size = inputStream.available();
                byte[] byteArray = new byte[size];
                inputStream.read(byteArray);
                inputStream.close();
                jsonString = new String(byteArray, "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
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
                        //event is not found in DB
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
                            values.put(Constants.DB_START_TIME, graphResult.getString("start_time"));
                        } catch (JSONException e) {
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

                    Log.d(Constants.LOG_DEBUG, "Update values: " + values.toString());
                    int count = getContentResolver().update(Uri.withAppendedPath(Constants.URI, "event/" + eventID), values, null, null);
                    Log.d(Constants.LOG_DEBUG, "Updated count: " + count);


                    // Check if there is extra data

                    /*String about = String.valueOf( graphObject.getProperty("about"));
                    String category =String.valueOf( graphObject.getProperty("category"));*/

                    /*message = about + "\n\n" +
                            "category: " + category ;*/

                } else if (error != null) {
                    //message = "Error getting request info";

                    Log.d(Constants.LOG_DEBUG, "Error getting request info");

                }
                //debugging
                /*Toast.makeText(getApplicationContext(),
                        message,
                        Toast.LENGTH_LONG).show();*/
            }
        });
        // Execute the request asynchronously.
        Request.executeBatchAsync(request);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_test_parsers, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

package com.mentormate.academy.fbpartyapp.json_parsers;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.mentormate.academy.fbpartyapp.R;
import com.mentormate.academy.fbpartyapp.Utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestParsers extends ActionBarActivity {

    private Pattern pattern = Pattern.compile("^https?://[^/]+/([^/]+)/([^/]+)/.*");
    private String segmentOne, eventID;

    private int eventsFound = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_parsers);

        //parseFeedJson();
        //Cursor c = getContentResolver().query(Uri.withAppendedPath(Constants.URI, "33"), null, null, null, "");
        Cursor c = getContentResolver().query(Uri.withAppendedPath(Constants.URI, "event/1562382110671319"), null, null, null, "");

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

    private void parseFeedJson() {

        //LOCAL JSON PARSING TEST
        InputStream inputStream;
        String jsonString = null;
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

                    Log.d(Constants.LOG_DEBUG, "Message: " + message + " segmentOne: " + segmentOne + " eventID: " + eventID);

                    //TODO save in DB
                    ContentValues values = new ContentValues();
                    values.put(Constants.DB_EVENT_ID, eventID);
                    values.put(Constants.DB_URL, message);

                    Log.d(Constants.LOG_DEBUG, "addEvent values: " + values.toString());
                    Uri uri = getContentResolver().insert(Constants.URI, values);
                    Log.d(Constants.LOG_DEBUG, "addEvent uri: " + uri.toString());

                    return true;
                }
            }
        }

        return false;
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

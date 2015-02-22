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
import com.mentormate.academy.fbpartyapp.R;
import com.mentormate.academy.fbpartyapp.Services.EventsDownloadService;
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


        //int count = getContentResolver().delete(Constants.URI, null, null);
        //Log.d(Constants.LOG_DEBUG, "Deleted " + count + " events.");


        //Cursor c = getContentResolver().query(Constants.URI, null, null, null, "");

        //Cursor c = getContentResolver().query(Uri.withAppendedPath(Constants.URI, "events_today"), null, null, null, "");

        //Cursor c = getContentResolver().query(Uri.withAppendedPath(Constants.URI, "33"), null, null, null, "");
        Cursor c = getContentResolver().query(Uri.withAppendedPath(Constants.URI, "event/806037962779425"), null, null, null, "");

        String result = "";
        if (!c.moveToFirst()) {
            Toast.makeText(this, result + " no content yet!", Toast.LENGTH_LONG).show();
        } else {
            do{
                result =
                        " id " +  c.getString(c.getColumnIndex(Constants.DB_ID)) +
                        " has event id: " + c.getString(c.getColumnIndex(Constants.DB_EVENT_ID)) +
                        " has url: " + c.getString(c.getColumnIndex(Constants.DB_URL)) +
                        " has start time: " + c.getString(c.getColumnIndex(Constants.DB_START_TIME));
                Log.d(Constants.LOG_DEBUG, result);
            } while (c.moveToNext());

        }
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

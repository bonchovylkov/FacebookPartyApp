package com.mentormate.academy.fbpartyapp.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;
import com.mentormate.academy.fbpartyapp.Utils.Constants;
import com.mentormate.academy.fbpartyapp.Utils.PopulateEventsData;
import com.mentormate.academy.fbpartyapp.Utils.SingletonSession;

import org.json.JSONObject;

/**
 * Created by Student11 on 2/19/2015.
 */
public class EventsDownloadService extends Service {

    public final static String ACTION_ASYNC = "com.mentormate.academy.fbpartyapp.Services.ACTION_ASYNC";
    public final static String BROADCAST_RESULT = "com.mentormate.academy.fbpartyapp.Services.BROADCAST_RESULT";
    public final static String KEY_MESSAGE = "events_downloaded";


    private Session session;
    private long lastUpdate = 0;
    private SharedPreferences sharedPreferences;
    private String segmentOne, eventID;


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
        Log.d(Constants.LOG_DEBUG, "session: " + session.toString());
        if(session == null || !session.isOpened())
        {
            //we should be here only with an active session
            //KILL service
            stopSelf();
        }

        Log.d(Constants.LOG_DEBUG, "start getRequestData");

        getRequestData(Constants.FB_PAGE_WHERE_IS_THE_PARTY_ID_FEED);

        Log.d(Constants.LOG_DEBUG, "after getRequestData");

        return super.onStartCommand(intent, flags, startId);
    }



    private void getRequestData(final String inRequestId) {

        //params -> ?since=<unix timestamp of last update>
        Bundle params = new Bundle();
        //TO DO -> remove after testing
        //lastUpdate = 0;
        if(lastUpdate != 0) {
            params.putString(Constants.FB_FEED_SINCE_PARAM, "" + lastUpdate);
        }

        //Log.d(Constants.LOG_DEBUG, "last_update: " + lastUpdate);

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
                if (graphObject != null) {

                    JSONObject graphResult = graphObject.getInnerJSONObject();

                    PopulateEventsData populateEventsData = new PopulateEventsData(EventsDownloadService.this);
                    populateEventsData.execute(graphResult.toString());

                    //set last update time
                    setLastUpdateTime();


                } else if (error != null) {
                    Log.d(Constants.LOG_DEBUG, "graphObject is null!");

                }
            }
        });
        Log.d(Constants.LOG_DEBUG, "1.Request: " + request);

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

package com.mentormate.academy.fbpartyapp.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;


public class PartiesDownloadService extends Service {

    public final static String ACTION_ASYNC = "com.mentormate.academy.fbpartyapp.Servises.ACTION_ASYNC";
    public final static String BROADCAST_RESULT = "com.mentormate.academy.fbpartyapp.Servises.BROADCAST_RESULT";
    public final static String KEY_MESSAGE = "message";
    public static String GRAPH_API_ID_LABEL ="GRAPH_API_ID_LABEL";
    private  String GRAPH_API_ID_VALUE="";

    public PartiesDownloadService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (ACTION_ASYNC.equals(intent.getAction())) {
            GRAPH_API_ID_VALUE = intent.getStringExtra(GRAPH_API_ID_LABEL);
            new WorkerThread().start();
        } else {
            if(!GRAPH_API_ID_VALUE.equals("")){
                getRequestData(GRAPH_API_ID_VALUE);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    //makes request to a certain id
    private void getRequestData(final String inRequestId) {
        // Create a new request for an HTTP GET with the
        // request ID as the Graph path.
        Request request = new Request(Session.getActiveSession(),
                inRequestId, null, HttpMethod.GET, new Request.Callback() {

            @Override
            public void onCompleted(Response response) {
                // Process the returned response
                GraphObject graphObject = response.getGraphObject();
                FacebookRequestError error = response.getError();
                // Default message
                String message = "Incoming request";
                if (graphObject != null) {
                    // Check if there is extra data

                    // try {

                    // about
                    String about = String.valueOf( graphObject.getProperty("about"));

                    String category =String.valueOf( graphObject.getProperty("category"));

                    message = about + "\n\n" +
                            "category: " + category ;

                    Log.d(PartiesDownloadService.class.getSimpleName(), message);

                    Intent intent = new Intent(BROADCAST_RESULT);
                    intent.putExtra(KEY_MESSAGE, message);
                    sendBroadcast(intent);

//                        } catch (JSONException e) {
//                            message = "Error getting request info";
//                        }
                } else if (error != null) {
                    message = "Error getting request info";

                }
//                Toast.makeText(getApplicationContext(),
//                        message,
//                        Toast.LENGTH_LONG).show();
            }
        });
        // Execute the request asynchronously.
        Request.executeBatchAsync(request);
    }



    private class WorkerThread extends Thread {
        @Override
        public void run() {
            super.run();
           if(!GRAPH_API_ID_VALUE.equals("")){
               getRequestData(GRAPH_API_ID_VALUE);
           }
        }
    }

}


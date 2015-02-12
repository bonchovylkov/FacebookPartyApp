package com.mentormate.academy.fbpartyapp.Fragments;



import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphPlace;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.mentormate.academy.fbpartyapp.Fragments.Utils.Constants;
import com.mentormate.academy.fbpartyapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Student11 on 2/9/2015.
 */
public class FacebookLoginFragment extends Fragment {

    private TextView userInfoTextView;
    private UiLifecycleHelper uiHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, container, false);
        userInfoTextView = (TextView) view.findViewById(R.id.userInfoTextView);
        LoginButton authButton = (LoginButton) view.findViewById(R.id.authButton);
        authButton.setFragment(this);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        // For scenarios where the main activity is launched and user
        // session is not null, the session state change notification
        // may not be triggered. Trigger it if it's open/closed.
        Session session = Session.getActiveSession();
        if (session != null &&
                (session.isOpened() || session.isClosed()) ) {
            onSessionStateChange(session, session.getState(), null);
        }

        uiHelper.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
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
//                        } catch (JSONException e) {
//                            message = "Error getting request info";
//                        }
                    } else if (error != null) {
                        message = "Error getting request info";

                }
                Toast.makeText(getActivity().getApplicationContext(),
                        message,
                        Toast.LENGTH_LONG).show();
            }
        });
        // Execute the request asynchronously.
        Request.executeBatchAsync(request);
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            Log.i(Constants.LOG_DEBUG, "Logged in...");

            // Request user data and show the results
            Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

                @Override
                public void onCompleted(GraphUser user, Response response) {
                    if (user != null) {
                        // Display the parsed user info
                        userInfoTextView.setVisibility(View.VISIBLE);

                        userInfoTextView.setText(buildUserInfoDisplay(user));
                        getRequestData("341486209377198");
                        getRequestData("341486209377198/feed");
                    }
                }
            });





        } else if (state.isClosed()) {
            Log.i(Constants.LOG_DEBUG, "Logged out...");
            userInfoTextView.setVisibility(View.INVISIBLE);
        }
    }

    private String buildUserInfoDisplay(GraphUser user) {
        StringBuilder userInfo = new StringBuilder("");

        Log.d("user",user.toString());
        int a =5 ;
        // Example: typed access (name)
        // - no special permissions required
        userInfo.append(String.format("Name: %s\n\n",
                user.getName()));

        // Example: typed access (birthday)
        // - requires user_birthday permission
       userInfo.append(String.format("Birthday: %s\n\n",
                user.getBirthday()));

        // Example: partially typed access, to location field,
        // name key (location)
        // - requires user_location permission
        userInfo.append(String.format("Location: %s\n\n",
                user.getLocation() != null ? user.getLocation().getProperty("name") : "No location"));

        // Example: access via property name (locale)
        // - no special permissions required
        userInfo.append(String.format("Locale: %s\n\n",
                user.getProperty("locale")));

        // Example: access via key for array (languages)
        // - requires user_likes permission
        JSONArray languages = (JSONArray)user.getProperty("languages");
        if (languages!=null) {
            if (languages.length() > 0) {
                ArrayList<String> languageNames = new ArrayList<String>();
                for (int i = 0; i < languages.length(); i++) {
                    JSONObject language = languages.optJSONObject(i);
                    // Add the language name to a list. Use JSON
                    // methods to get access to the name field.
                    languageNames.add(language.optString("name"));
                }
                userInfo.append(String.format("Languages: %s\n\n",
                        languageNames.toString()));
            }
        }
        return userInfo.toString();
    }

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };
}

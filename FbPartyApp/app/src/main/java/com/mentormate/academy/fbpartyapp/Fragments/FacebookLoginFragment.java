package com.mentormate.academy.fbpartyapp.Fragments;



import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.mentormate.academy.fbpartyapp.Fragments.Utils.Constants;
import com.mentormate.academy.fbpartyapp.R;
import com.mentormate.academy.fbpartyapp.Services.PartiesDownloadService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Student11 on 2/9/2015.
 */
public class FacebookLoginFragment extends Fragment {

    private TextView userInfoTextView;
    private TextView lbServiceResult;
    private UiLifecycleHelper uiHelper;
    private Context currentContext;

    public FacebookLoginFragment() {
    }

    public FacebookLoginFragment(Context currentContext) {
        this.currentContext = currentContext;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, container, false);
        userInfoTextView = (TextView) view.findViewById(R.id.userInfoTextView);
        lbServiceResult=(TextView) view.findViewById(R.id.lbServiceResult);
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
                        lbServiceResult.setVisibility(View.VISIBLE);

                        userInfoTextView.setText(buildUserInfoDisplay(user));

                        Intent intent = new Intent("test");
                        intent.putExtra("lbTest", "Resulttest");
                        currentContext.sendBroadcast(intent);

                       // startDownloadService();

                       // getRequestData("341486209377198");
                      //  getRequestData("341486209377198/feed");
                    }
                }
            });





        } else if (state.isClosed()) {
            Log.i(Constants.LOG_DEBUG, "Logged out...");
            userInfoTextView.setVisibility(View.INVISIBLE);
            lbServiceResult.setVisibility(View.INVISIBLE);
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

package com.mentormate.academy.fbpartyapp.Fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;
import com.mentormate.academy.fbpartyapp.Parties;
import com.mentormate.academy.fbpartyapp.R;
import com.mentormate.academy.fbpartyapp.Utils.Constants;
import com.mentormate.academy.fbpartyapp.Utils.SingletonSession;

import java.util.Arrays;

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

    /*public FacebookLoginFragment(Context currentContext) {
        this.currentContext = currentContext;
    }*/

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        currentContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, container, false);
        userInfoTextView = (TextView) view.findViewById(R.id.userInfoTextView);
        lbServiceResult=(TextView) view.findViewById(R.id.lbServiceResult);

        LoginButton authButton = (LoginButton) view.findViewById(R.id.authButton);
        authButton.setReadPermissions(Arrays.asList("user_likes"));
        authButton.setFragment(this);
        authButton.setReadPermissions(Arrays.asList("user_likes"));

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);

        Log.d(Constants.LOG_DEBUG, "onCreate()");

    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(Constants.LOG_DEBUG, "onResume()");

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

        Log.d(Constants.LOG_DEBUG, "onPause()");
    }

    @Override
    public void onDestroy() {
        Log.d(Constants.LOG_DEBUG, "onDestroy()");

        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
        Log.d(Constants.LOG_DEBUG, "onSaveInstanceState()");
    }

    private void onSessionStateChange(final Session session, SessionState state, Exception exception) {
        Log.d(Constants.LOG_DEBUG, "onSessionStateChange()");

       SingletonSession.getInstance().setCurrentSession();

        if (state.isOpened()) {
            Log.i(Constants.LOG_DEBUG, "Logged in...");

            Intent intent = new Intent(currentContext, Parties.class);
            intent.putExtra(Constants.INITIAL_STARTUP, 1);
            startActivity(intent);


        } else if (state.isClosed()) {
            Log.i(Constants.LOG_DEBUG, "Logged out...");

            Session currentSession = SingletonSession.getInstance().getCurrentSession();
            currentSession.closeAndClearTokenInformation();

            /*userInfoTextView.setVisibility(View.INVISIBLE);
            lbServiceResult.setVisibility(View.INVISIBLE);*/
        }
    }

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };
}

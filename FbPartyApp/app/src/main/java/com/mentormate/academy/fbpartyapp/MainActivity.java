package com.mentormate.academy.fbpartyapp;

import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.mentormate.academy.fbpartyapp.Fragments.FacebookLoginFragment;
import com.mentormate.academy.fbpartyapp.Fragments.Utils.Constants;


public class MainActivity extends FragmentActivity {

    private LoginButton loginBtn;
    private FacebookLoginFragment facebookLoginFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            // Add the fragment on initial activity setup
            facebookLoginFragment = new FacebookLoginFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, facebookLoginFragment)
                    .commit();

//            loginBtn = (LoginButton) findViewById(R.id.authButton);
//            loginBtn.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {
//                @Override
//                public void onUserInfoFetched(GraphUser user) {
//                    if (user != null) {
//                        Log.d(Constants .LOG_DEBUG,"Hello, " + user.getName());
//                    } else {
//                        Log.d(Constants .LOG_DEBUG,"You are not logged");
//                    }
//                }
//            });
        } else {
            // Or set the fragment from restored state info
            facebookLoginFragment = (FacebookLoginFragment) getSupportFragmentManager()
                    .findFragmentById(android.R.id.content);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

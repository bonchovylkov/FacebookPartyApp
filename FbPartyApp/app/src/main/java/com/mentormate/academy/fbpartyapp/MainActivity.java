package com.mentormate.academy.fbpartyapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.facebook.widget.LoginButton;
import com.mentormate.academy.fbpartyapp.Fragments.FacebookLoginFragment;

import com.mentormate.academy.fbpartyapp.Services.PartiesDownloadService;
import com.mentormate.academy.fbpartyapp.Utils.Constants;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class MainActivity extends FragmentActivity {

    private LoginButton loginBtn;
    private FacebookLoginFragment facebookLoginFragment;
    private TextView lbServiceResult;

//    BroadcastReceiver receiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (intent.hasExtra("lbTest")) {
//                startDownloadService();
//
//            }
//        }
//    };


    private void startDownloadService() {
        Intent intent = new Intent(this, PartiesDownloadService.class);
        intent.setAction(PartiesDownloadService.ACTION_ASYNC);
        intent.putExtra(PartiesDownloadService.GRAPH_API_ID_LABEL, Constants.FB_PAGE_WHERE_IS_THE_PARTY_ID_FEED);
        startService(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //IF you get an error: "Invalid keyhash"
        // Uncomment below lines and look for "KeyHash:" in Logcat.
        // Copy that keyhash in the FB app settings;
        /*
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.mentormate.academy.fbpartyapp",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }*/


//        registerReceiver(receiver,
//                new IntentFilter(PartiesDownloadService.BROADCAST_RESULT));

        if (savedInstanceState == null) {
            // Add the fragment on initial activity setup
            facebookLoginFragment = new FacebookLoginFragment(getBaseContext());
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

    @Override
    protected void onStop() {
        super.onStop();
//        if(receiver!=null){
//            unregisterReceiver(receiver);
//        }

    }
}

package com.mentormate.academy.fbpartyapp;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.mentormate.academy.fbpartyapp.Fragments.FacebookLoginFragment;

public class MainActivity extends FragmentActivity {

    private FacebookLoginFragment facebookLoginFragment;


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

        if (savedInstanceState == null) {
            // Add the fragment on initial activity setup
            facebookLoginFragment = new FacebookLoginFragment(getBaseContext());
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, facebookLoginFragment)
                    .commit();

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
    }

}

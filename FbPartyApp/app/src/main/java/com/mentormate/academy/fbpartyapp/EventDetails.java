package com.mentormate.academy.fbpartyapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LikeView;
import com.mentormate.academy.fbpartyapp.Models.Event;
import com.mentormate.academy.fbpartyapp.Utils.Constants;
import com.mentormate.academy.fbpartyapp.Utils.SingletonSession;
import com.squareup.picasso.Picasso;


public class EventDetails extends Activity  {

    TextView lbEventName;
    TextView lbEventStartTime;
    TextView lbEventDescription;
    ImageView eventImage;
    private UiLifecycleHelper uiHelper;
    Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        Intent intent = getIntent();
        event = (Event)intent.getSerializableExtra(Constants.INTENT_EVENT_EXTRA_PARAM);

        Settings.sdkInitialize(this);

        LikeView likeView = (LikeView) findViewById(R.id.like_view);
        likeView.setObjectId("http://www.facebook.com/"+Constants.FB_PAGE_WHERE_IS_THE_PARTY_ID);

        lbEventDescription =(TextView) findViewById(R.id.lbEventDescription);
        lbEventStartTime =(TextView) findViewById(R.id.lbEventStartTime);
        lbEventName =(TextView) findViewById(R.id.lbEventName);
        eventImage =  (ImageView)findViewById(R.id.eventImage);

        lbEventDescription.setText(event.getDescription());
        lbEventStartTime.setText(event.getStartTime());
        lbEventName.setText(event.getName());
        Picasso.with(this).load(event.getCoverSource()).into(eventImage);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_event_details, menu);
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


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

Log.d(Constants.LOG_DEBUG,"onActivityResult:" + requestCode + " " + resultCode);
       // uiHelper.onActivityResult(requestCode, resultCode, data, null);
        // if you don't use the UiLifecycleHelper, call handleOnActivityResult on the LikeView instead
         LikeView.handleOnActivityResult(this, requestCode, resultCode, data);

    }


//    private Session.StatusCallback callback = new Session.StatusCallback() {
//        @Override
//        public void call(Session session, SessionState state, Exception exception) {
//
//            Log.d(Constants.LOG_DEBUG,"callback:" + session.toString());
//
//
//
//
//        }
//    };

//    @Override
//    public void onClick(View v) {
//        startActivityForResult(new Intent(this,VisusActivity.class), 0);
//    }
}

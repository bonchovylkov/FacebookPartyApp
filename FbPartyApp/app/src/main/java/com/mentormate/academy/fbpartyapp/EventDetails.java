package com.mentormate.academy.fbpartyapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.mentormate.academy.fbpartyapp.Models.Event;
import com.mentormate.academy.fbpartyapp.Utils.Constants;
import com.mentormate.academy.fbpartyapp.Utils.SingletonSession;
import com.squareup.picasso.Picasso;


public class EventDetails extends FragmentActivity {

    private GoogleMap mMap;
    private TextView lbEventName;
    private TextView lbEventStartTime;
    private TextView lbEventDescription;
    private ImageView eventImage;
    private UiLifecycleHelper uiHelper;
    Event event;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        setUpMapIfNeeded();

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

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {

        //ANIMATE CAMERA
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(42.64653915, 23.37826252), 14.0f));

        //MOVE CAMERA
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(42.64653915, 23.37826252), 14.0f));

        // ADD MARKERS
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(42.64653915, 23.37826252))
                .title("MentorMate Academy Sofia"));
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(42.13591336, 24.74219799))
                .title("MentorMate Academy Plovdiv")
                .draggable(true)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .alpha(0.7f)
                .flat(true));

        // CHANGE MAP TYPE
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        GoogleMapOptions options = new GoogleMapOptions();
        // MORE OPTIONS CUSTOMIZATION
        options.compassEnabled(true)
                .rotateGesturesEnabled(true)
                .tiltGesturesEnabled(true);

        // DRAWING ON THE MAP
        // Instantiates a new CircleOptions object and defines the center and radius
        CircleOptions circleOptions = new CircleOptions()
                .center(new LatLng(42.64653915, 23.37826252))
                .radius(1000); // In meters

        // Get back the mutable Circle
        Circle circle = mMap.addCircle(circleOptions);

        // Instantiates a new Polygon object and adds points to define a rectangle
        PolygonOptions rectOptions = new PolygonOptions()
                .add(new LatLng(42.64653915, 23.37826252),
                        new LatLng(42.65653915, 23.37826252),
                        new LatLng(42.65653915, 23.38826252),
                        new LatLng(42.64653915, 23.38826252),
                        new LatLng(42.64653915, 23.37826252));

        // Get back the mutable Polygon
        Polygon polygon = mMap.addPolygon(rectOptions);

        /* STREET VIEW
        // Displays an image of the Swiss Alps
        Uri gmmIntentUri = Uri.parse("google.streetview:cbll=46.414382,10.013988");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
        */
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

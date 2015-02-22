package com.mentormate.academy.fbpartyapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.Settings;
import com.facebook.widget.LikeView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mentormate.academy.fbpartyapp.Models.Event;
import com.mentormate.academy.fbpartyapp.Utils.Constants;
import com.squareup.picasso.Picasso;


public class EventDetails extends FragmentActivity {

    private GoogleMap mMap;
    private TextView lbEventName,lbEventStartTime, lbEventDescription;
    private ImageView eventImage;

    private String eventLat, eventLng;
    private LatLng location;
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

        eventLat = event.getLat();
        eventLng = event.getLng();
        Log.d(Constants.LOG_DEBUG, "EventDetails lat lng: " + this.eventLat + " " + this.eventLng);

        if(eventLng != null && !eventLng.equals("") && eventLat != null && !eventLat.equals(""))
        {
            location = new LatLng(
                Double.parseDouble(eventLat),
                Double.parseDouble(eventLng)
            );
            Log.d(Constants.LOG_DEBUG, "EventDetails location: " + location.toString());
        }
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

        if(location != null)
        {
            Log.d(Constants.LOG_DEBUG, location.toString());

            //ANIMATE CAMERA
            //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 14.0f));
            //42.6931,23.3225

            // ADD MARKERS
            mMap.addMarker(new MarkerOptions()
                .position(location)
                .title(event.getName())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .alpha(0.7f)
            );

            // CHANGE MAP TYPE
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

            GoogleMapOptions options = new GoogleMapOptions();
            // MORE OPTIONS CUSTOMIZATION
            options.compassEnabled(true)
                    .rotateGesturesEnabled(true)
                    .tiltGesturesEnabled(true);


            /* STREET VIEW
            // Displays an image of the Swiss Alps
            Uri gmmIntentUri = Uri.parse("google.streetview:cbll=46.414382,10.013988");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
            */
        }


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

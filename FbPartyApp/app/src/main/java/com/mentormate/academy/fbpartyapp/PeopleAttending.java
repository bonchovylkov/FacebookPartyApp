package com.mentormate.academy.fbpartyapp;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;
import com.mentormate.academy.fbpartyapp.CustomAdapters.PeopleAttendingAdapter;
import com.mentormate.academy.fbpartyapp.Fragments.PeopleAttendingFragment;
import com.mentormate.academy.fbpartyapp.Models.Event;
import com.mentormate.academy.fbpartyapp.Models.PersonFB;
import com.mentormate.academy.fbpartyapp.Utils.Constants;
import com.mentormate.academy.fbpartyapp.Utils.SingletonSession;

import java.util.ArrayList;


public class PeopleAttending extends FragmentActivity {

    Event event;
    TextView textView;
    private Session session;
    PeopleAttendingFragment peopleAttendingFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.peope_attending);
       Intent intent = getIntent();
       // textView = (TextView) findViewById(R.id.lbAttendingPeople);
        session = SingletonSession.getInstance().getCurrentSession();
        event = (Event)intent.getSerializableExtra(Constants.INTENT_EVENT_EXTRA_PARAM);
        getRequestData(event.getEventId()+"/attending"  );

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_people_attending, menu);
        return true;
    }

    private void getRequestData(final String inRequestId) {


        Bundle params = new Bundle();



        // request ID as the Graph path.
        Request request = new Request(session,
                inRequestId, params, HttpMethod.GET, new Request.Callback() {

            @Override
            public void onCompleted(Response response) {
                // Process the returned response
                GraphObject graphObject = response.getGraphObject();

                FacebookRequestError error = response.getError();

                // Default message
                if (graphObject != null) {

                   // textView.setText(graphObject.toString());

                    ArrayList<PersonFB> list = new ArrayList<PersonFB>();
                    PersonFB p = new PersonFB();
                    p.setName("Pehso");
                    p.setId(1);
                    list.add(p);

                    PeopleAttendingAdapter adapter = new PeopleAttendingAdapter(getApplicationContext(), list);
                    peopleAttendingFragment = new PeopleAttendingFragment(adapter);

                    FragmentTransaction transaction = getFragmentManager().beginTransaction();

// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack
                    transaction.replace(R.id.peopleAttendingViewFragment, peopleAttendingFragment);
                    transaction.addToBackStack(null);

// Commit the transaction
                    transaction.commit();

                } else if (error != null) {
                    textView.setText("graphObject=null");
                    Log.d(Constants.LOG_DEBUG, "graphObject is null!");

                }
            }
        });
        Log.d(Constants.LOG_DEBUG, "1.Request: " + request);

        // Execute the request asynchronously.
        Request.executeBatchAsync(request);

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

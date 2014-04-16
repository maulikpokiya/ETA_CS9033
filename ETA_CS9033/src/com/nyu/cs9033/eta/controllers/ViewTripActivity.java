package com.nyu.cs9033.eta.controllers;

import java.util.ArrayList;
import java.util.Iterator;

import com.nyu.cs9033.eta.models.Person;
import com.nyu.cs9033.eta.models.Trip;
import com.nyu.cs9033.eta.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ViewTripActivity extends Activity {

	TextView viewLocation = null;
	TextView viewDate = null;
//	TextView viewTime = null;
	TextView viewFriends = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_view_trip);
		setTitle("View Trip");
		
		viewLocation = (TextView)findViewById(R.id.tripLocation);
	    viewDate = (TextView)findViewById(R.id.tripDate);
//	    viewTime = (TextView)findViewById(R.id.tripTime);
	    viewFriends = (TextView)findViewById(R.id.tripFriends);
		Trip trip = getTripFromIntent(getIntent());
		initView(trip);
	}
	
	/**
	 * Create the most recent trip that
	 * was passed to TripViewer.
	 * 
	 * @param i The Intent that contains
	 * the most recent trip data.
	 * 
	 * @return The Trip that was most recently
	 * passed to TripViewer, or null if there
	 * is none.
	 */
	public Trip getTripFromIntent(Intent i) {
		ArrayList<Trip> trips = i.getExtras().getParcelableArrayList("parcel");
	
//ArrayList<Trip> trips = i.getParcelableArrayListExtra("viewTrip"); 
		Trip trip = trips.get(0);
		return trip;
	}

	/**
	 * Populate the View using a Trip model.
	 * 
	 * @param trip The Trip model used to
	 * populate the View.
	 */
	public void initView(Trip trip) {
		
		if(trip != null) {
		viewLocation.setText(trip.getmDestination());
		viewDate.setText(trip.getmDate().toString());
//		viewTime.setText(trip.getTime());
		Iterator<Person> itr = trip.getPeople().iterator();
		while(itr.hasNext()) {
			viewFriends.append(itr.next().getName());
		}
		
//			viewLocation.setText(trip.toString());
		}
		else {
			new AlertDialog.Builder(this)  
            .setIcon(android.R.drawable.ic_dialog_alert)  
            .setTitle("Alert")  
            .setMessage("No trips found. Please try again later.")  
            .setPositiveButton("Confirm", null)  
            .show();  
            return;
		}
	}
	
	public void cancelTripView(View view) {

		// TODO - fill in here
//		setResult(RESULT_CANCELED);
		finish();
	}
}

package com.nyu.cs9033.eta.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.nyu.cs9033.eta.models.Person;
import com.nyu.cs9033.eta.models.Trip;

import com.nyu.cs9033.eta.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ViewTripActivity extends Activity {

	TextView viewLocation = null;
	TextView viewDate = null;
	// TextView viewTime = null;
	TextView viewFriends = null;
	TextView mGpsInfo = null;

	private LocationManager GpsManager;
	private final String TAG = "ViewTripActivity";
	double distanceToDestination = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_view_trip);
		setTitle("View Trip");

		viewLocation = (TextView) findViewById(R.id.tripLocation);
		viewDate = (TextView) findViewById(R.id.tripDate);
		// viewTime = (TextView)findViewById(R.id.tripTime);
		viewFriends = (TextView) findViewById(R.id.tripFriends);
		Trip trip = getTripFromIntent();
		initView(trip);
	}

	public void gpstracking(View view) {
		// GPS tracking
		mGpsInfo = (TextView) this.findViewById(R.id.gpslocation);
		GpsManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		Location location = GpsManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		printGpsLocation(location);
		if (location == null) {
			// mGpsInfo.setText("GPS is disabled");
			Toast.makeText(this,
					"GPS is disabled. Please turn it on in the setting page",
					Toast.LENGTH_LONG).show();
			Intent callGPSSettingIntent = new Intent(
					android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivity(callGPSSettingIntent);
		}
		// update location in minimum 10 minimum intervals and 1000 meters.
		GpsManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				1000 * 300, 1000, new GpsLocationListener());

		/**
		 * if it is near destination OR passed the scheduled time stop GPS
		 * service
		 */
		/*
		 * if(distanceToDestination<=500||Util.compare_with_Current(
		 * getTripFromIntent().getDate())){ //onPause(); }
		 */
	}

	public void printGpsLocation(Location location) {
		Double[] destination = null;
		if (location != null) {
			try {
				destination = getLongitudeAndLatitudeFromAddress();
				double distance = getDistance(location.getLatitude(),
						location.getLongitude(), destination[0], destination[1]);
				distanceToDestination = Math
						.round(java.lang.Math.abs(distance));
				mGpsInfo.setText("Your current location: \n" + "Latitude £º"
						+ location.getLatitude() + "\nLongitude : "
						+ location.getLongitude() + "\nDate time: "
						+ location.getTime() + "\nDistance to "
						+ getTripFromIntent().getLocation() + " (meter): "
						+ distanceToDestination);

				/*
				 * Intent intent = new Intent(); intent.putExtra("altitude",
				 * String.valueOf(location.getAltitude()));
				 * intent.putExtra("latitude",
				 * String.valueOf(location.getLatitude()));
				 * intent.putExtra("time", String.valueOf(location.getTime()));
				 */

			} catch (IndexOutOfBoundsException e) {
				Toast.makeText(this, "Cannot locate input location", Toast.LENGTH_SHORT).show();
				System.out.println("Cannot locat input location");
			}
		}
	}

	private double getDistance(double lat1, double lon1, double lat2,
			double lon2) {
		float[] results = new float[1];
		Location.distanceBetween(lat1, lon1, lat2, lon2, results);
		return results[0];
	}

	/**
	 * Create the most recent trip that was passed to TripViewer.
	 * 
	 * @param i
	 *            The Intent that contains the most recent trip data.
	 * 
	 * @return The Trip that was most recently passed to TripViewer, or null if
	 *         there is none.
	 */
	public Trip getTripFromIntent() {

		ArrayList<Trip> trips = getIntent().getExtras().getParcelableArrayList(
				"parcel");
		Trip trip = trips.get(0);
		return trip;
	}

	/**
	 * Convert Address to Longitude And Latitude
	 * 
	 * @return
	 */
	public Double[] getLongitudeAndLatitudeFromAddress() {
		Geocoder geocoder = new Geocoder(getBaseContext());
		List<Address> addresses;
		double latitude = 0, longitude = 0;

		try {
			addresses = geocoder.getFromLocationName(getTripFromIntent()
					.getLocation(), 20);
			Address addr = addresses.get(0);
			latitude = addr.getLatitude();
			longitude = addr.getLongitude();
			System.out.println("latitude: " + latitude + " longitude: "
					+ longitude);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Double[] location = { latitude, longitude };
		return location;

	}

	/**
	 * Populate the View using a Trip model.
	 * 
	 * @param trip
	 *            The Trip model used to populate the View.
	 */
	public void initView(Trip trip) {

		if (trip != null) {
			viewLocation.setText(trip.getmDestination());
			viewDate.setText(trip.getmDate().toString());
			// viewTime.setText(trip.getTime());
			Iterator<Person> itr = trip.getPeople().iterator();
			while (itr.hasNext()) {
				viewFriends.append(itr.next().getName());
			}

			// viewLocation.setText(trip.toString());
		} else {
			new AlertDialog.Builder(this)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle("Alert")
					.setMessage("No trips found. Please try again later.")
					.setPositiveButton("Confirm", null).show();
			return;
		}
	}

	public void cancelTripView(View view) {

		// TODO - fill in here
		// setResult(RESULT_CANCELED);
		finish();
	}

	public class GpsLocationListener implements LocationListener {
		public void onLocationChanged(Location location) {
			printGpsLocation(location);
		}

		public void onProviderDisabled(String provider) {
			Log.d(TAG, "ProviderDisabled : " + provider);
		}

		public void onProviderEnabled(String provider) {
			Log.d(TAG, "ProviderEnabled : " + provider);
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			Log.d(TAG, "StatusChanged : " + provider + status);
		}
	}
}

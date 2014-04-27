package com.nyu.cs9033.eta.controllers;

import org.json.JSONObject;

import com.nyu.cs9033.eta.R;
import com.nyu.cs9033.eta.db.TripDatabaseHelper;
import com.nyu.cs9033.eta.json.JSONParser;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

public class MainActivity extends Activity {

	// private static Trip trip = null;

	TripDatabaseHelper tdh = new TripDatabaseHelper(this);
	JSONParser jsonParser = new JSONParser();

	// url to create new trip
	private static String trip_status_url = "http://cs9033-homework.appspot.com/";
	private static String trip_status_command = "TRIP_STATUS";

	private TextView status;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		status = (TextView) findViewById(R.id.tvStatus);
		long tripId = getCurrentTripId();

		if (tripId != 0) {
			getTripStatus(tripId);
		}
	}

	/**
	 * This method should start the Activity responsible for creating a Trip.
	 */
	public void startTripCreator(View view) {

		Intent intent = new Intent(this, CreateTripActivity.class);
		startActivityForResult(intent, 1);
	}

	/**
	 * This method should start the Activity responsible for viewing a Trip.
	 */
	public void viewTripHistory(View view) {

		Intent intent = new Intent(this, TripHistoryActivity.class);
		startActivity(intent);
	}

	/**
	 * getting current trip status in background thread
	 * 
	 * @param id
	 */
	public void getTripStatus(long id) {

		new GetTripStatus().execute(trip_status_command, "" + id + "");
	}

	/**
	 * Gets current trip id from database.
	 * 
	 * @return id
	 */
	public long getCurrentTripId() {
		long id = tdh.getCurrentTripId();
		return id;
	}

	/**
	 * Background Async Task to get trip status
	 * */
	class GetTripStatus extends AsyncTask<String, String, String> {

		// Progress Dialog
		private ProgressDialog pDialog;

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(MainActivity.this);
			pDialog.setMessage("Getting Current Trip Status..");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Getting status
		 * */
		protected String doInBackground(String... args) {

			// String status = "Failure";
			JSONObject json = null;
			ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = connMgr.getActiveNetworkInfo();
			if (netInfo != null & netInfo.isConnected()) {
				JSONObject jsonObject = new JSONObject();
				try {
					jsonObject.accumulate("command", args[0]);
					jsonObject.accumulate("trip_id", args[1]);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// getting JSON Object
				json = jsonParser.makeHttpRequest(trip_status_url, "POST",
						jsonObject);

				// check log cat for response
				Log.d("JSON",
						"JSON Response in MainActivity :" + json.toString());

				// status = "Success";
			} else {
				// No network connection
				MainActivity.this.runOnUiThread(new Runnable() {
					public void run() {
						// your alert dialog builder here
						new AlertDialog.Builder(MainActivity.this)
								.setIcon(android.R.drawable.ic_dialog_alert)
								.setTitle("Error")
								.setMessage("Network not found.")
								.setPositiveButton("OK", null).show();
					}
				});
			}
			return json.toString();
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String result) {
			status.setText(result);
			// dismiss the dialog after getting all products
			pDialog.dismiss();
		}

		@Override
		protected void onCancelled() {
			Log.i("Cancel", "onCancelled() called");
			pDialog = new ProgressDialog(MainActivity.this);
			pDialog.setMessage("onCancelled called");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();

		}

	}
}

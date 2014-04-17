package com.nyu.cs9033.eta.controllers;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

import com.nyu.cs9033.eta.db.TripDatabaseHelper;
import com.nyu.cs9033.eta.json.JSONParser;
import com.nyu.cs9033.eta.models.Person;
import com.nyu.cs9033.eta.models.Trip;
import com.nyu.cs9033.eta.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class CreateTripActivity extends Activity {

	private int hour;
	private int minute;
	private int seconds = 00;
	private int year;
	private int month;
	private int day;
	static final int DATE_DIALOG_ID = 2;
	static final int TIME_DIALOG_ID = 1;

	private TextView tvDisplayDate;
	private TextView tvDisplayTime;
	private Button btnChangeDate;
	private Button btnChangeTime;

	private TextView tvTripLocation;
	private TextView tvTripFriends;
	TripDatabaseHelper tdh = new TripDatabaseHelper(this);
	ArrayList<String> allNames = new ArrayList<String>();

	JSONParser jsonParser = new JSONParser();

	// url to create new trip
	private static String create_trip_url = "http://cs9033-homework.appspot.com/";
	private static String create_trip_command = "CREATE_TRIP";

	// JSON Node names
	private static final String TAG_RESPONSE = "response_code";
	private static final String TAG_TRIP = "trip_id";

	Intent intent = new Intent();
	private static final int REQUEST_CONTACT = 2;

	private Date tripDate;
	private Long tripTime;
	private static String tripLocation;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_createtrip);
		setTitle("Create Trip");
		tvTripLocation = (TextView) findViewById(R.id.tripLocation);
		tvTripFriends = (TextView) findViewById(R.id.tripFriends);
		tvDisplayDate = (TextView) findViewById(R.id.tvDate);
		tvDisplayTime = (TextView) findViewById(R.id.tvTime);

		Intent intent = getIntent();
		if (isMapIntent(intent)) {

			String mapdata = intent.getStringExtra(Intent.EXTRA_TEXT);

			if (mapdata != null) {
				tvTripLocation.setText(mapdata);
			}
		}

		setCurrentDateTimeOnView();
		addListenerOnButton();
	}

	/**
	 * This method should be used to instantiate a Trip model.
	 * 
	 * @return The Trip as represented by the View.
	 * @throws ParseException
	 */
	public void createTrip(View view) throws ParseException {

		String location = tvTripLocation.getText().toString().trim();
		String date = tvDisplayDate.getText().toString().trim();
		String time = tvDisplayTime.getText().toString().trim();
		String friends = tvTripFriends.getText().toString().trim();

		if (TextUtils.isEmpty(location) || TextUtils.isEmpty(date)
				|| TextUtils.isEmpty(time) || TextUtils.isEmpty(friends)) {
			Toast.makeText(this, "All fields are mendatory.", Toast.LENGTH_LONG)
					.show();
		} else {
			Calendar cal = Calendar.getInstance();
			cal.set(year, month, day, hour, minute, seconds);
			tripDate = cal.getTime();
			tripTime = tripDate.getTime() / 1000L;
			tripLocation = location;

			// creating new trip in background thread
			new CreateNewTrip().execute(create_trip_command, location);
		}
		Intent i = new Intent(this, MainActivity.class);
		startActivity(i);
	}

	public Trip setTrip() {
		ArrayList<Person> allPerson = new ArrayList<Person>();
		Trip p = new Trip();
		p.setmDestination(tripLocation);
		p.setmDate(tripDate);
		for (String name : allNames) {
			Person newPerson = new Person();
			newPerson.setName(name);
			allPerson.add(newPerson);
		}
		p.setPeople(allPerson);
		return p;
	}

	// display current date time
	public void setCurrentDateTimeOnView() {

		final Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);
		hour = c.get(Calendar.HOUR_OF_DAY);
		minute = c.get(Calendar.MINUTE);

		// tripDate = new Date(year, month, day);
		// set current date into textview
		tvDisplayDate.setText(new StringBuilder()
				// Month is 0 based, just add 1
				.append(month + 1).append("/").append(day).append("/")
				.append(year).append(" "));

		// set current time into textview
		tvDisplayTime.setText(new StringBuilder().append(pad(hour)).append(":")
				.append(pad(minute)));
	}

	public void addListenerOnButton() {

		btnChangeDate = (Button) findViewById(R.id.btnChangeDate);
		btnChangeTime = (Button) findViewById(R.id.btnTripTime);

		btnChangeDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				showDialog(DATE_DIALOG_ID);

			}

		});

		btnChangeTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				showDialog(TIME_DIALOG_ID);
			}
		});
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			// set date picker as current date
			return new DatePickerDialog(this, datePickerListener, year, month,
					day);

		case TIME_DIALOG_ID:
			// set time picker as current time
			return new TimePickerDialog(this, timePickerListener, hour, minute,
					false);
		}
		return null;
	}

	private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

		// when dialog box is closed, below method will be called.
		public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {
			year = selectedYear;
			month = selectedMonth;
			day = selectedDay;

			// set selected date into textview
			tvDisplayDate.setText(new StringBuilder().append(month + 1)
					.append("/").append(day).append("/").append(year)
					.append(" "));
		}
	};

	private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int selectedHour,
				int selectedMinute) {
			hour = selectedHour;
			minute = selectedMinute;

			// set current time into textview
			tvDisplayTime.setText(new StringBuilder().append(pad(hour))
					.append(":").append(pad(minute)));
		}
	};

	private static String pad(int c) {
		if (c >= 10)
			return String.valueOf(c);
		else
			return "0" + String.valueOf(c);
	}

	/**
	 * For HW2 you should treat this method as a way of sending the Trip data
	 * back to the main Activity
	 * 
	 * Note: If you call finish() here the Activity eventually end and pass an
	 * Intent back to the previous Activity using setResult().
	 * 
	 * @return whether the Trip was successfully persisted.
	 */
	public boolean persistTrip(Trip trip) {

		long id = tdh.insertTrip(trip);
		Iterator<Person> itr = trip.getPeople().iterator();
		while (itr.hasNext()) {
			tdh.insertFriends(id, itr.next());
		}
		Intent intent = new Intent(CreateTripActivity.this, MainActivity.class);
		setResult(RESULT_OK, intent);
		finish();
		return true;
	}

	/**
	 * This method should be used when a user wants to cancel the creation of a
	 * Trip.
	 * 
	 * Note: You most likely want to call this if your activity dies during the
	 * process of a trip creation or if a cancel/back button event occurs.
	 * Should return to the previous activity without a result using finish()
	 * and setResult().
	 */
	public void cancelTripCreation(View view) {

		setResult(RESULT_CANCELED);
		finish();
	}

	public boolean isMapIntent(Intent intent) {

		final String action = intent.getAction();
		if (Intent.ACTION_SEND.equals(action)) {

			final String type = intent.getType();
			if ("text/plain".equals(type)) {

				return true;
			} else {
				new AlertDialog.Builder(this)
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setTitle("Alert").setMessage("No data in intent.")
						.setPositiveButton("Confirm", null).show();
			}
		}
		return false;
	}

	public void addFriends(View view) {
		Intent i = new Intent(Intent.ACTION_PICK,
				ContactsContract.Contacts.CONTENT_URI);
		startActivityForResult(i, REQUEST_CONTACT);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			/*
			 * ContentResolver resolver = getContentResolver(); Cursor contacts
			 * = resolver.query(Contacts.CONTENT_URI, null,
			 * Contacts.HAS_PHONE_NUMBER + " != 0", null, Contacts._ID +
			 * " ASC"); int idIndex =
			 * contacts.getColumnIndexOrThrow(Contacts._ID); int nameIndex =
			 * contacts.getColumnIndexOrThrow(Contacts.DISPLAY_NAME);
			 * List<String> allNames = new ArrayList<String>(); String name =
			 * ""; final EditText phoneInput = (EditText)
			 * findViewById(R.id.tripFriends);
			 * 
			 * while (contacts.moveToNext()) { // long id =
			 * contacts.getLong(idIndex); name = contacts.getString(nameIndex);
			 * allNames.add(name); // System.out.println("Contact(" + id + "): "
			 * + contacts.getString(nameIndex));
			 * 
			 * } final CharSequence[] items = allNames.toArray(new
			 * String[allNames.size()]); AlertDialog.Builder builder = new
			 * AlertDialog.Builder(CreateTripActivity.this); //
			 * builder.setTitle("Choose a number"); builder.setItems(items, new
			 * DialogInterface.OnClickListener() { public void
			 * onClick(DialogInterface dialog, int item) { String selectedNumber
			 * = items[item].toString(); // selectedNumber =
			 * selectedNumber.replace("-", "");
			 * phoneInput.setText(selectedNumber); } }); AlertDialog alert =
			 * builder.create(); if(allNames.size() > 1) { alert.show(); } else
			 * { String selectedNumber = name.toString(); // selectedNumber =
			 * selectedNumber.replace("-", "");
			 * phoneInput.setText(selectedNumber); // }
			 */
			// final TextView phoneInput = (EditText)
			// findViewById(R.id.tripFriends);
			Cursor cursor = null;
			String phoneNumber = "", name = "";
			List<String> allNumbers = new ArrayList<String>();
			// List<String> allNames = new ArrayList<String>();
			int phoneIdx = 0;
			try {
				Uri result = data.getData();
				String id = result.getLastPathSegment();
				cursor = getContentResolver().query(
						Phone.CONTENT_URI,
						null,
						Phone.CONTACT_ID + "=? AND "
								+ Contacts.HAS_PHONE_NUMBER + " != 0",
						new String[] { id }, null);

				// int idIndex = cursor.getColumnIndexOrThrow(Contacts._ID);
				int nameIndex = cursor
						.getColumnIndexOrThrow(Contacts.DISPLAY_NAME);
				phoneIdx = cursor.getColumnIndex(Phone.DATA);
				if (cursor.moveToFirst()) {
					while (cursor.isAfterLast() == false) {
						phoneNumber = cursor.getString(phoneIdx);
						name = cursor.getString(nameIndex);
						allNumbers.add(phoneNumber);
						allNames.add(name);
						cursor.moveToNext();
					}
				} else {
					// no results actions
				}
			} catch (Exception e) {
				// error actions
			} finally {
				if (cursor != null) {
					cursor.close();
				}

				final CharSequence[] items = allNames
						.toArray(new String[allNames.size()]);
				AlertDialog.Builder builder = new AlertDialog.Builder(
						CreateTripActivity.this);
				builder.setTitle("Choose a number");
				builder.setItems(items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						String selectedNumber = items[item].toString();
						// selectedNumber = selectedNumber.replace("-", "");
						tvTripFriends.append(selectedNumber);
					}
				});
				AlertDialog alert = builder.create();
				if (allNumbers.size() > 1) {
					alert.show();
				} else {
					String selectedNumber = name.toString();
					// selectedNumber = selectedNumber.replace("-", "");
					tvTripFriends.append(selectedNumber);
				}

				if (phoneNumber.length() == 0) {
					// no numbers found actions
				}
			}

		} else {
			// activity result error actions
		}
	}

	/**
	 * Background Async Task to Create new trip
	 * */
	class CreateNewTrip extends AsyncTask<String, String, String> {

		// Progress Dialog
		private ProgressDialog pDialog;

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(CreateTripActivity.this);
			pDialog.setMessage("Creating Trip..");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			// pDialog.show();
		}

		/**
		 * Creating user
		 * */
		protected String doInBackground(String... args) {

			String status = "Failure";
			ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = connMgr.getActiveNetworkInfo();
			if (netInfo != null & netInfo.isConnected()) {
				JSONObject jsonObject = new JSONObject();
				try {
					jsonObject.accumulate("command", args[0]);
					jsonObject.accumulate("location", args[1]);
					jsonObject.accumulate("datetime", tripTime);
					jsonObject.accumulate("people", allNames);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// getting JSON Object
				JSONObject json = jsonParser.makeHttpRequest(create_trip_url,
						"POST", jsonObject);

				// check log cat for response
				Log.d("JSON",
						"JSON Response in CreateTripActivity :"
								+ json.toString());

				// check for success tag
				try {
					int response = json.getInt(TAG_RESPONSE);

					if (response == 0) {
						long trip_id = json.getLong(TAG_TRIP);
						Trip trip = setTrip();
						trip.setmId(trip_id);
						persistTrip(trip);
						intent.setClass(CreateTripActivity.this,
								MainActivity.class);
						CreateTripActivity.this.startActivity(intent);
						CreateTripActivity.this.finish();
					} else {
						// failed to create User
						CreateTripActivity.this.runOnUiThread(new Runnable() {
							public void run() {
								// your alert dialog builder here
								new AlertDialog.Builder(CreateTripActivity.this)
										.setIcon(
												android.R.drawable.ic_dialog_alert)
										.setTitle("Error")
										.setMessage(
												"Oops, Error occurred while creating trip.")
										.setPositiveButton("OK", null).show();
							}
						});

					}
				} catch (JSONException je) {
					Log.e("ERROR", "Error in CreateTripActivity.createTrip()"
							+ je.toString());
				}
				status = "Success";
			} else {
				// No network connection
				CreateTripActivity.this.runOnUiThread(new Runnable() {
					public void run() {
						// your alert dialog builder here
						new AlertDialog.Builder(CreateTripActivity.this)
								.setIcon(android.R.drawable.ic_dialog_alert)
								.setTitle("Error")
								.setMessage("Network not found.")
								.setPositiveButton("OK", null).show();
					}
				});
			}
			return status;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			// pDialog.dismiss();
		}

	}
}

package com.nyu.cs9033.eta.controllers;

import com.nyu.cs9033.eta.R;

import android.os.Bundle;
import android.view.View;
import android.app.Activity;
import android.content.Intent;

public class MainActivity extends Activity {

	// private static Trip trip = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
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
}

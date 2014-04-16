package com.nyu.cs9033.eta.controllers;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.nyu.cs9033.eta.R;
import com.nyu.cs9033.eta.models.Trip;
import com.nyu.cs9033.eta.db.TripDatabaseHelper;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class TripHistoryActivity extends ListActivity {

	ListView listView;
	TripDatabaseHelper tdh = new TripDatabaseHelper(this);;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trip_history);

		listView = (ListView) findViewById(android.R.id.list);
		listView.setAdapter(new myAdapter(this));
	}

	private class myAdapter extends BaseAdapter {

		private Context mContext;

		public myAdapter(Context context) {
			this.mContext = context;
		}

		@Override
		public int getCount() {
			try {
				return getAllTrips().size();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.item, null);
				ItemViewCache viewCache = new ItemViewCache();
				viewCache.lTextView = (TextView) convertView
						.findViewById(R.id.ltext);
				convertView.setTag(viewCache);
			}
			try{
				
			
			ItemViewCache cache = (ItemViewCache) convertView.getTag();

			cache.lTextView.setText(getAllTrips().get(position).getmDate()
					+ "\n" + getAllTrips().get(position).getmDestination());
			}
			catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return convertView;
		}
	}

	private static class ItemViewCache {
		public TextView lTextView;

	}

	public List<Trip> getAllTrips() throws ParseException {
		List<Trip> trips = new ArrayList<Trip>();
		trips = tdh.getAllTrips();
		return trips;
	}

	/**
	 * This method is responsible for clicking single event and get into
	 * ViewTripActivity
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		ArrayList<Trip> trips = new ArrayList<Trip>();
		Trip curTrip;
		try {
			curTrip = getAllTrips().get(position);
			trips.add(curTrip);
			Intent intent = new Intent(this, ViewTripActivity.class);
			intent.putParcelableArrayListExtra("parcel", trips);
			startActivity(intent);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

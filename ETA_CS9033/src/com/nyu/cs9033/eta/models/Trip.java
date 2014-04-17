package com.nyu.cs9033.eta.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class Trip implements Parcelable {

	/*
	 * private String location = null; private String date = null; // private
	 * String time = null; private String friends = null;
	 */
	private long mId;
	private String mDestination;
	private Date mDate;
	// private String mDate;
	private List<Person> people; // could be list of objects too
	// private ArrayList<String> people;

	// private static final String JSON_ID = "id";
	private static final String JSON_DESTINATION = "destination";
	private static final String JSON_PEOPLE = "people";
	private static final String JSON_DATE = "date";

	public Trip() {

		people = new ArrayList<Person>();
		// mId = UUID.randomUUID();
		mDate = new Date();
		mDestination = null;
	}

	public static final Parcelable.Creator<Trip> CREATOR = new Parcelable.Creator<Trip>() {
		public Trip createFromParcel(Parcel p) {
			Long dateLong = 0L;
			Trip trip = new Trip();
			dateLong = p.readLong();
			trip.mDate = new Date(dateLong);
			trip.mDestination = p.readString();
			p.readTypedList(trip.people, Person.CREATOR);
			return trip;
			// return new Trip(p);
		}

		public Trip[] newArray(int size) {
			return new Trip[size];
		}
	};

	/**
	 * Create a Trip model object from a Parcel
	 * 
	 * @param p
	 *            The Parcel used to populate the Model fields.
	 */
	public Trip(Parcel p) {

		/*
		 * mDate = p.readString(); mDestination = p.readString();
		 * p.readTypedList(people, Person.CREATOR);
		 */
		/*
		 * String[] data = new String[2]; Log.v(TAG,
		 * "ParcelData(Parcel source): time to put back parcel data");
		 * p.readStringArray(data); this.mDestination = data[0]; this.mDate =
		 * data[1]; this.time = data[2]; this.people = data[2];
		 */}

	public void writeToParcel(Parcel dest, int arg1) {
		
		// Log.v(TAG, "writeToParcel..."+ flags);
		dest.writeLong(mDate.getTime());
		dest.writeString(mDestination);
		dest.writeTypedList(people);
		// dest.writeStringArray(new String[] { this.mDestination, this.mDate,
		// this.people.toString() });
	}

	/**
	 * Feel free to add additional functions as necessary below.
	 */

	public String getmDestination() {
		return mDestination;
	}

	public void setmDestination(String mDestination) {
		this.mDestination = mDestination;
	}

	public long getmId() {
		return mId;
	}

	public void setmId(long mId) {
		this.mId = mId;
	}

	public Date getmDate() {
		return mDate;
	}

	public void setmDate(Date mDate) {
		this.mDate = mDate;
	}

	public List<Person> getPeople() {
		return people;
	}

	public void setPeople(ArrayList<Person> people) {
		this.people = people;
	}

	/**
	 * Do not implement
	 */
	@Override
	public int describeContents() {
		// Do not implement!
		return 0;
	}

	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();
		// json.put(JSON_ID, mId.toString());
		json.put(JSON_DESTINATION, mDestination);
		json.put(JSON_PEOPLE, new JSONArray(people));
		json.put(JSON_DATE, mDate.getTime());
		return json;
	}
}

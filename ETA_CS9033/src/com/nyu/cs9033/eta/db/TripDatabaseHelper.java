package com.nyu.cs9033.eta.db;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.nyu.cs9033.eta.models.Person;
import com.nyu.cs9033.eta.models.Trip;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TripDatabaseHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 3;
	private static final String DATABASE_NAME = "trips";

	private static final String TABLE_TRIP = "trip";
	private static final String COLUMN_TRIP_ID = "id"; // convention
	private static final String COLUMN_TRIP_DATE = "date";
	private static final String COLUMN_TRIP_DESTINATION = "destination";
	private static final String COLUMN_SERVER_TRIP_ID = "server_trip_id";

	private static final String TABLE_FRIENDS = "friends";
	private static final String COLUMN_FRND_TRIPID = "trip_id";
	private static final String COLUMN_FRND_ID = "id";
	private static final String COLUMN_FRND_NAME = "name";

	public TripDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// create trip table
		db.execSQL("create table " + TABLE_TRIP + "(" + COLUMN_TRIP_ID
				+ " integer primary key autoincrement, " + COLUMN_TRIP_DATE
				+ " varchar(10), " + COLUMN_TRIP_DESTINATION + " text," + COLUMN_SERVER_TRIP_ID + " integer)");
		// create location table
		db.execSQL("create table " + TABLE_FRIENDS + "(" + COLUMN_FRND_ID
				+ " integer primary key autoincrement, " + COLUMN_FRND_TRIPID
				+ " integer references trip(id), " + COLUMN_FRND_NAME
				+ " varchar(20))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		// Drop older table if exists
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRIP);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FRIENDS);

		// create tables again
		onCreate(db);
	}

	public long insertTrip(Trip trip) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_TRIP_DATE, trip.getmDate().toString());
		cv.put(COLUMN_TRIP_DESTINATION, trip.getmDestination());
		cv.put(COLUMN_SERVER_TRIP_ID, trip.getmId());
		// return id of new trip
		return getWritableDatabase().insert(TABLE_TRIP, null, cv);
	}
	
/*	public long updateTrip(Trip trip) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_TRIP_DATE, trip.getmDate().toString());
		cv.put(COLUMN_TRIP_DESTINATION, trip.getmDestination());
		cv.put(COLUMN_SERVER_TRIP_ID, trip.getmId());
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("select id from " + TABLE_TRIP + " where id = (select max(" + COLUMN_TRIP_ID + ") from " + TABLE_TRIP + ")", null);
		
		// return id of new trip
		return getWritableDatabase().insert(TABLE_TRIP, null, cv);
	}
*/
	public long insertFriends(long id, Person friend) {
		ContentValues cv = new ContentValues();
//		SQLiteDatabase db = this.getReadableDatabase();
//		Cursor cursor = db.rawQuery("select id from " + TABLE_TRIP + " where id = (select max(" + COLUMN_TRIP_ID + ") from " + TABLE_TRIP + ")", null);
		
		cv.put(COLUMN_FRND_TRIPID, id);
		cv.put(COLUMN_FRND_NAME, friend.getName());
		// return id of new trip
		return getWritableDatabase().insert(TABLE_FRIENDS, null, cv);
	}
	
	/*
	 * public long insertLocation(long tripId, Location location) {
	 * ContentValues cv = new ContentValues(); 
	 * cv.put(COLUMN_LOC_TRIPID, tripId); 
	 * cv.put(COLUMN_LOC_TIMESTAMP, location.getTime());
	 * cv.put(COLUMN_LOC_LAT, location.getLatitude()); 
	 * cv.put(COLUMN_LOC_LONG, location.getLongitude()); 
	 * cv.put(COLUMN_LOC_ALT, location.getAltitude());
	 * cv.put(COLUMN_LOC_PROVIDER, location.getProvider()); 
	 * // return id of new
	 * location return getWritableDatabase().insert(TABLE_LOCATION, null, cv); }
	 */
	
	public List<Trip> getAllTrips() throws ParseException {
		List<Trip> tripList = new ArrayList<Trip>();

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from " + TABLE_TRIP, null);
		
		int id = 0;

		// loop through all query results
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			Trip trip = new Trip();
			id = cursor.getInt(0);
			Cursor cursorFrnd = db.rawQuery("select name from " + TABLE_FRIENDS + " where " + COLUMN_FRND_TRIPID + "=?", new String[] {""+id+""});
//			trip.setmId(cursor.getInt(0));
			
//			SimpleDateFormat curFormater = new SimpleDateFormat("MM/dd/yyyy", Locale.US); 
//			Date dateObj = curFormater.parse(cursor.getString(1));
			DateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy", Locale.US);
			Date date = (Date)formatter.parse(cursor.getString(1));
			trip.setmDate(date);
			trip.setmDestination(cursor.getString(2));
			trip.setmId(cursor.getLong(3));
			ArrayList<Person> people = new ArrayList<Person>();
			for (cursorFrnd.moveToFirst(); !cursorFrnd.isAfterLast(); cursorFrnd.moveToNext()) {
				Person p = new Person();
				p.setName(cursorFrnd.getString(0));
				people.add(p);
			}
			trip.setPeople(people);
			tripList.add(trip);
		}
		return tripList;
	}

}

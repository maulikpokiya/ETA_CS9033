package test;

import java.text.ParseException;
import java.util.List;

import android.test.AndroidTestCase;

import com.nyu.cs9033.eta.controllers.TripHistoryActivity;
import com.nyu.cs9033.eta.models.Trip;


public class Test extends AndroidTestCase {

	TripHistoryActivity tripHistoryActivity = new TripHistoryActivity();
	
	public void getAll(){
		List<Trip> trips = null;
		try {
			trips = tripHistoryActivity.getAllTrips();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(Trip trip: trips){
			System.out.println("----BF---");
			System.out.println(trip.toString());
			System.out.println("----AF---");
		}
	}

}

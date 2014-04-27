package com.nyu.cs9033.eta.models;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class Gps {
	private Location location = null;
	private LocationManager locationManager = null;
	private Context context = null;

	/**
	 * @param ctx
	 */
	public Gps(Context ctx) {
		context = ctx;
		locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		location = locationManager.getLastKnownLocation(getProvider());
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				5000, 10, locationListener);
	}

	// Location Provider
	private String getProvider() {

		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		return locationManager.getBestProvider(criteria, true);
	}

	private LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location l) {
			if (l != null) {
				location = l;
			}
		}

		public void onProviderDisabled(String provider) {
			location = null;
		}

		public void onProviderEnabled(String provider) {
			Location l = locationManager.getLastKnownLocation(provider);
			if (l != null) {
				location = l;
			}

		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

	};

	public Location getLocation() {
		return location;
	}

	public void closeLocation() {
		if (locationManager != null) {
			if (locationListener != null) {
				locationManager.removeUpdates(locationListener);
				locationListener = null;
			}
			locationManager = null;
		}
	}
}
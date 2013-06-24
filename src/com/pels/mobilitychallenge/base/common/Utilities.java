package com.pels.mobilitychallenge.base.common;

import com.pels.mobilitychallenge.base.data.Reading;

public class Utilities {

	public static double distance(Reading r1, Reading r2) {
		double ans = distance(r1.getLatitude(), r1.getLongitude(), r2.getLatitude(), r2.getLongitude());
		return ans;
	}

	/*
	 * Calculate distance between two points in latitude and longitude taking
	 * into account height difference. If you are not interested in height
	 * difference pass 0.0. Uses Haversine method as its base.
	 * 
	 * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
	 * el2 End altitude in meters
	 */

	private static double distance(double lat1, double lon1, double lat2, double lon2) {
		final int R = 6371; // Radius of the earth

		Double latDistance = deg2rad(lat2 - lat1);
		Double lonDistance = deg2rad(lon2 - lon1);
		Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(deg2rad(lat1))
				* Math.cos(deg2rad(lat2)) * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
		Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double distance = R * c * 1000; // convert to meters

		return distance;
	}

	/**
	 * This function converts decimal degrees to radians
	 */
	private static double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}
}

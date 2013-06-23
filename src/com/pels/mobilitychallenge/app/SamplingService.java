package com.pels.mobilitychallenge.app;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.pels.mobilitychallenge.app.common.SamplingMode;
import com.pels.mobilitychallenge.app.common.Session;

public class SamplingService extends Service {

	private final IBinder mBinder = new SamplingServiceBinder();

	private NotificationManager mNM;
	private int NOTIFICATION = 007;

	private LocationManager locationManager;
	private LocationListener locationListener = new MyLocationListener();

	private File currentLogFile;
	private BufferedOutputStream writer = null;

	public class SamplingServiceBinder extends Binder {
		SamplingService getService() {
			return SamplingService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public void onCreate() {
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

	}

	public boolean startSampling() {
		if (setUpLogFile()) {
			Session.setSampling(true);
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
			showNotification();
			return true;
		} else {
			Session.setSampling(false);
			return false;
		}
	}

	private boolean setUpLogFile() {
		boolean ret = false;
		String filePath = Session.getStoragePath() + File.separator + Session.getCurrentFileName();
		currentLogFile = new File(filePath);

		if (!currentLogFile.exists()) {
			ret = createAndWriteHeaderLine();
		} else {
			try {
				writer = new BufferedOutputStream(new FileOutputStream(currentLogFile, true));
				ret = true;
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return ret;
	}

	private boolean createAndWriteHeaderLine() {
		boolean ret = false;
		try {
			currentLogFile.getParentFile().mkdirs();
			writer = new BufferedOutputStream(new FileOutputStream(currentLogFile));
			String header = "time,latitude,longitude,elevation,accuracy,bearing,speed,label\n";
			writer.write(header.getBytes());
			writer.flush();
			ret = true;
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), "Could not create log file", Toast.LENGTH_LONG).show();
		}
		return ret;
	}

	public void stopSampling() {
		if (Session.isSampling()) {
			locationManager.removeUpdates(locationListener);
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			mNM.cancel(NOTIFICATION);
			Session.setNotificationVisible(false);
			Session.setSampling(false);
			Session.setStarted(false);
			makeToast(getText(R.string.recoding_trip_service_started));
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		String modeName = intent.getStringExtra("sampling_mode");

		if (modeName != null) {
			Session.setCurrentSamplingMode(SamplingMode.valueOf(modeName));
			startSampling();
		}

		return START_REDELIVER_INTENT;
	}

	@Override
	public void onDestroy() {
		stopSampling();

	}

	public class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			if (location != null)
				appendLocationToFile(location);
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}

	}

	/**
	 * Show a notification while this service is running.
	 */
	@SuppressWarnings("deprecation")
	private void showNotification() {
		// What happens when the notification item is clicked
		Intent contentIntent = new Intent(this, MainActivity.class);

		PendingIntent pending = PendingIntent.getActivity(getApplicationContext(), 0, contentIntent,
				android.content.Intent.FLAG_ACTIVITY_NEW_TASK);

		Notification nfc = new Notification(R.drawable.ic_launcher, null, System.currentTimeMillis());
		nfc.flags |= Notification.FLAG_ONGOING_EVENT;

		String contentText = getString(R.string.recoding_trip_notification_text);
		String contentTitle = getString(R.string.recoding_trip_notification_title);
		nfc.setLatestEventInfo(getApplicationContext(), contentTitle, contentText, pending);

		mNM.notify(NOTIFICATION, nfc);
		Session.setNotificationVisible(true);
	}

	public void appendLocationToFile(Location loc) {
		StringBuilder sb = new StringBuilder();
		sb.append(loc.getTime() + ",").append(loc.getLatitude() + ",").append(loc.getLongitude() + ",")
				.append(loc.getAltitude() + ",").append(loc.getAccuracy() + ",").append(loc.getBearing() + ",")
				.append(loc.getSpeed() + ",");
		String outputString = sb.toString() + "\n";
		// String outputString = String.format(Locale.US,
		// "%d,%d,%d,%d,%d,%d,%d,%s\n",
		// loc.getTime(), loc.getLatitude(),
		// loc.getLongitude(), loc.getAltitude(), loc.getAccuracy(),
		// loc.getBearing(), loc.getSpeed(), currentMode.toString());
		Log.i("OUTPUT_STRING", outputString);
		appendNewLine(outputString);
		Session.increaseReadingsCounter();
		Session.setLatestTimeStamp(loc.getTime());
	}

	private void appendNewLine(String outputString) {
		if (Session.isSampling() && writer != null) {
			try {
				writer.write(outputString.getBytes());
				writer.flush();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				makeToast("Could not append new reading: " + outputString);
			}
		}
	}

	private void makeToast(CharSequence string) {
		Toast.makeText(getApplicationContext(), string, Toast.LENGTH_LONG).show();

	}
}

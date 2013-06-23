package com.pels.mobilitychallenge.app.common;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Application;
import android.os.Environment;

public class Session extends Application {

	// ---------------------------------------------------
	// Session values - keeping track as app runs
	// ---------------------------------------------------
	private static boolean gpsEnabled;
	private static boolean isSampling;
	private static String currentFileName;
	private static boolean notificationVisible;
	private static long latestTimeStamp;
	private static int readingsCount;
	private static long samplingStartTime;
	private static long samplingStopTime;
	private static SamplingMode currentSamplingMode;
	private static boolean debugMode;
	private static String storagePath = Environment
			.getExternalStorageDirectory()
			+ File.separator
			+ "MobilityChallengeSampler";
	private static SimpleDateFormat dateFormat = new SimpleDateFormat(
			"dd-MM-yy", Locale.getDefault());
	private static DateFormat timeFormat = DateFormat.getTimeInstance();
	private static Date date;

	/**
	 * @return whether GPS (satellite) is enabled
	 */
	public static boolean isGpsEnabled() {
		return gpsEnabled;
	}

	/**
	 * @param gpsEnabled
	 *            set whether GPS (satellite) is enabled
	 */
	public static void setGpsEnabled(boolean gpsEnabled) {
		Session.gpsEnabled = gpsEnabled;
	}

	/**
	 * @return whether logging has started
	 */
	public static boolean isStarted() {
		return isSampling;
	}

	/**
	 * @param isStarted
	 *            set whether logging has started
	 */
	public static void setStarted(boolean isStarted) {
		Session.isSampling = isStarted;

	}

	/**
	 * @return the currentFileName (without extension)
	 */
	public static String getCurrentFileName() {
		if (currentFileName == null) {
			currentFileName = dateFormat.format(date) + ".csv";
		}
		return currentFileName;
	}

	/**
	 * @return the notificationVisible
	 */
	public static boolean isNotificationVisible() {
		return notificationVisible;
	}

	/**
	 * @param notificationVisible
	 *            the notificationVisible to set
	 */
	public static void setNotificationVisible(boolean notificationVisible) {
		Session.notificationVisible = notificationVisible;
	}

	/**
	 * @return the latestTimeStamp (for location info)
	 */
	public static long getLatestTimeStamp() {
		return latestTimeStamp;
	}

	/**
	 * @return the latestTimeStamp in Time of date (for location info)
	 */
	public static String getLatestTimeStampLabel() {
		if (latestTimeStamp == 0) {
			return "--";
		}
		return timeFormat.format(new Date(latestTimeStamp));
	}

	/**
	 * @param latestTimeStamp
	 *            the latestTimeStamp (for location info) to set
	 */
	public static void setLatestTimeStamp(long latestTimeStamp) {
		Session.latestTimeStamp = latestTimeStamp;
	}

	/**
	 * @return the isSampling
	 */
	public static boolean isSampling() {
		return isSampling;
	}

	/**
	 * @param isSampling
	 *            the isSampling to set
	 */
	public static void setSampling(boolean isSampling) {
		Session.isSampling = isSampling;
	}

	/**
	 * @return the readingsCount
	 */
	public static int getReadingsCount() {
		return readingsCount;
	}

	/**
	 * @param readingsCount
	 *            the readingsCount to set
	 */
	public static void setReadingsCount(int readingsCount) {
		Session.readingsCount = readingsCount;
	}

	/**
	 * @return the samplingStartTime
	 */
	public static long getSamplingStartTime() {
		return samplingStartTime;
	}

	/**
	 * @param samplingStartTime
	 *            the samplingStartTime to set
	 */
	public static void setSamplingStartTime(long samplingStartTime) {
		Session.samplingStartTime = samplingStartTime;
	}

	/**
	 * @return the samplingStopTime
	 */
	public static long getSamplingStopTime() {
		return samplingStopTime;
	}

	public static long getSamplingDuration() {
		return samplingStopTime - samplingStartTime;
	}

	public static String getSamplingDurationLabel() {
		long dur = samplingStopTime - samplingStartTime;
		int sec = (int) (dur / 1000) % 60;
		int min = (int) ((dur / (1000 * 60)) % 60);

		return String.format("%dm %02ds", min, sec);
	}

	/**
	 * @param samplingStopTime
	 *            the samplingStopTime to set
	 */
	public static void setSamplingStopTime(long samplingStopTime) {
		Session.samplingStopTime = samplingStopTime;
	}

	/**
	 * @return the currentSamplingMode
	 */
	public static SamplingMode getCurrentSamplingMode() {
		return currentSamplingMode;
	}

	/**
	 * @param currentSamplingMode
	 *            the currentSamplingMode to set
	 */
	public static void setCurrentSamplingMode(SamplingMode currentSamplingMode) {
		Session.currentSamplingMode = currentSamplingMode;
	}

	public static String getStoragePath() {
		// TODO Auto-generated method stub
		return storagePath;
	}

	public static void increaseReadingsCounter() {
		readingsCount++;

	}

	public static void resetSummary() {
		readingsCount = 0;
		latestTimeStamp = 0;

	}

	public static boolean isDebugToFile() {
		// TODO Auto-generated method stub
		return debugMode;
	}

	public static Date getDate() {
		return date;
	}

	public static void setDate(Date date) {
		Session.date = date;
	}

}

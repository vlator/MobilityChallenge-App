package com.pels.mobilitychallenge.app;

import java.io.File;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.pels.mobilitychallenge.app.common.Session;
import com.pels.mobilitychallenge.base.data.Label;
import com.pels.mobilitychallenge.data.TravelReport;

public class MainActivity extends Activity {
	private static Intent serviceIntent;
	private SamplingService samplingService;

	private Date currentDate;
	private File reportFile;

	private ToggleButton startStopToggle;
	private Button generateReportBtn;
	private TextView dateLabel;
	private TextView todayGpsLabel;
	private TextView todayDistanceLabel;
	private TextView todayBikeTracksLabel;
	private TextView todayAvgSpeedLabel;
	private TextView todayTopSpeedLabel;

	private static final String DATE_DISPLAY_FORMAT = "MMMM dd, yyyy";


	/**
	 * Provides a connection to the GPS Sampling Service
	 */
	private final ServiceConnection samplingServiceConnection = new ServiceConnection() {

		public void onServiceDisconnected(ComponentName name) {
			samplingService = null;
		}

		public void onServiceConnected(ComponentName name, IBinder service) {
			samplingService = ((SamplingService.SamplingServiceBinder) service).getService();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		serviceIntent = new Intent(this, SamplingService.class);
		startAndBindService();

		// Get refs to UI elements
		startStopToggle = (ToggleButton) findViewById(R.id.startStopToggle);
		generateReportBtn = (Button) findViewById(R.id.generateReport);
		dateLabel = (TextView) findViewById(R.id.todayDate);
		todayGpsLabel = (TextView) findViewById(R.id.todayGpsCount);
		todayDistanceLabel = (TextView) findViewById(R.id.todayDistanceCovered);
		todayAvgSpeedLabel = (TextView) findViewById(R.id.todayAvgSpeed);
		todayTopSpeedLabel = (TextView) findViewById(R.id.todayTopSpeed);
		todayBikeTracksLabel = (TextView) findViewById(R.id.todayBikeTracks);
		// --------

		currentDate = new Date(System.currentTimeMillis());
		Session.setDate(currentDate);

		reportFile = getReportFile();
	}

	private File getReportFile() {
		String fullPath = Session.getStoragePath() + File.separator + Session.getCurrentFileName() + ".report";
		File f = new File(fullPath);
		if (f.exists()) {
			updateDisplay(f);
		}
		return f;
	}

	private void updateDisplay(File oldReportFile) {
		TravelReport report = TravelReport.parse(oldReportFile);
		if (report != null) {
			updateDisplay(report);
		} else {
			showToast("Could not parse existing report file: " + oldReportFile.getName());
		}
	}

	/**
	 * Starts the service and binds the activity to it.
	 */
	private void startAndBindService() {
		startService(serviceIntent);
		bindService(serviceIntent, samplingServiceConnection, Context.BIND_AUTO_CREATE);
		// samplingService.startSampling();
	}

	public void onStartStopClicked(View view) {
		boolean on = ((ToggleButton) view).isChecked();
		if (on) {
			startSamplingService();
		} else {
			stopSamplingService();
		}

	}

	public void generateReport(View view) {
		try {
			TravelReport report = new AnalysisTask(this).execute((Void[])null).get();
			if (report != null) {
				updateDisplay(report);
				writeTravelReportToFile(report);
			}
		} catch (InterruptedException e) {
			showToast("Analysis task was interrupted! Please try again later.");
		} catch (ExecutionException e) {
			showToast("Analysis task was not completed! Please try again later.");
		}
	}

	private void writeTravelReportToFile(TravelReport report) {
		if (reportFile.exists()) {
			showToast("Exisitng Report File will be overwritten!");
		}
		TravelReport.writeToFile(reportFile, report);
	}

	private void showToast(String string) {
		Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
	}

	private void updateDisplay(TravelReport report) {
		dateLabel.setText(DateFormat.format(DATE_DISPLAY_FORMAT, currentDate));
		todayGpsLabel.setText(report.getGpsCount(Label.BIKE) + " GPS traces");
		todayAvgSpeedLabel.setText(report.getAverageSpeed(Label.BIKE) + "m/s");
		todayTopSpeedLabel.setText(report.getTopSpeed(Label.BIKE) + "m/s");
		todayDistanceLabel.setText(report.getDistanceCovered(Label.BIKE) + "m");
		todayBikeTracksLabel.setText(report.getTracksCount(Label.BIKE) + " tracks");
	}

	private void startSamplingService() {
		boolean success = samplingService.startSampling();
		if (success) {
			Session.setSamplingStartTime(System.currentTimeMillis());
		} else {
			startStopToggle.toggle();
		}
		generateReportBtn.setEnabled(!success);
	}

	private void stopSamplingService() {
		samplingService.stopSampling();
		Session.setSamplingStopTime(System.currentTimeMillis());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}

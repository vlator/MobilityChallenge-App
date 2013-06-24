package com.pels.mobilitychallenge.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.format.DateFormat;

import com.pels.mobilitychallenge.app.common.Session;
import com.pels.mobilitychallenge.base.data.FramedReadings;
import com.pels.mobilitychallenge.base.data.FramesExtractor;
import com.pels.mobilitychallenge.base.data.Label;
import com.pels.mobilitychallenge.base.data.Reading;
import com.pels.mobilitychallenge.base.data.ReadingsLoader;
import com.pels.mobilitychallenge.data.TravelReport;

public class AnalysisTask extends AsyncTask<Void, Integer, TravelReport> {
	private static final String DATE_DISPLAY_FORMAT = "MMMM dd, yyyy";
	private static final String ERR_MSG = "Could not find log for today to run analysis on.";

	private static final int WINDOW_LENGTH = 20; // secs

	ReadingsLoader readingsLoader;
	FramesExtractor frameExtractor;
	Classifier classifier;

	Date todaysDate;
	String dateString;
	File todaysRawFile;

	ProgressDialog pd;
	Context context;

	public AnalysisTask(Context context) {
		this.context = context;
		todaysDate = Session.getDate();
		dateString = (String) DateFormat.format(DATE_DISPLAY_FORMAT, todaysDate);
		
		todaysRawFile = new File(Session.getStoragePath() + File.separator + Session.getCurrentFileName() + ".csv");
		classifier = new SimpleClassifier();
	}

	private TravelReport run() throws FileNotFoundException {
		if (!todaysRawFile.exists()) {
			throw new FileNotFoundException(ERR_MSG);
		}

		TravelReport travelReport = null;

		readingsLoader = new ReadingsLoader(todaysRawFile);
		List<Reading> allReadings = readingsLoader.getAllReadings();

		frameExtractor = new FramesExtractor(WINDOW_LENGTH);
		List<FramedReadings> allFrames = frameExtractor.getFramedReadings(allReadings);

		for (FramedReadings frame : allFrames) {
			Label l = classifier.classifier(frame.getFeatureVector());
			frame.setLabel(l);
		}

		travelReport = TravelReport.extractTravelReport(allFrames);

		return travelReport;
	}

	@Override
	protected void onPreExecute() {
		pd = new ProgressDialog(context);
		pd.setTitle("Analyzing Log for Today: " + dateString);
		pd.setMessage("Please wait.");
		pd.show();
	}

	@Override
	protected TravelReport doInBackground(Void... params) {
		TravelReport report = null;
		try {
			Thread.sleep(5000);
			report = run();
			if (report == null){
				pd.setMessage("Log file for today did not produce any travel report");
				pd.dismiss();
			}
		} catch (FileNotFoundException e) {
			String str = "Could not run analysis. No log file was found for today.";
			pd.setMessage(str);
			pd.dismiss();  
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return report;
	}
	
    @Override
    protected void onPostExecute(TravelReport result) {
            pd.dismiss();
    }
}

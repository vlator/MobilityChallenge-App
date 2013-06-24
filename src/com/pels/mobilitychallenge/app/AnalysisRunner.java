package com.pels.mobilitychallenge.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.List;

import com.pels.mobilitychallenge.app.common.Session;
import com.pels.mobilitychallenge.base.data.FramedReadings;
import com.pels.mobilitychallenge.base.data.FramesExtractor;
import com.pels.mobilitychallenge.base.data.Label;
import com.pels.mobilitychallenge.base.data.Reading;
import com.pels.mobilitychallenge.base.data.ReadingsLoader;
import com.pels.mobilitychallenge.data.TravelReport;

public class AnalysisRunner {
	private static final String ERR_MSG = "Could not find log for today to run analysis on.";

	private static final int WINDOW_LENGTH = 20; //secs
	
	ReadingsLoader readingsLoader;
	FramesExtractor frameExtractor;
	Classifier classifier;
	
	Date todaysDate;
	File todaysRawFile;
	
	
	public AnalysisRunner(){
		todaysDate = Session.getDate();
		todaysRawFile = new File(Session.getStoragePath() + File.separator + Session.getCurrentFileName() + ".csv");
		classifier = new SimpleClassifier();
	}

	public TravelReport run() throws FileNotFoundException{
		if (!todaysRawFile.exists()){
			throw new FileNotFoundException(ERR_MSG);
		}
		
		TravelReport travelReport = null;
		
		readingsLoader = new ReadingsLoader(todaysRawFile);
		List<Reading> allReadings = readingsLoader.getAllReadings();
		
		frameExtractor = new FramesExtractor(WINDOW_LENGTH);
		List<FramedReadings> allFrames = frameExtractor.getFramedReadings(allReadings);
		
		for (FramedReadings frame : allFrames){
			Label l = classifier.classifier(frame.getFeatureVector());
			frame.setLabel(l);
		}
		
		travelReport = TravelReport.extractTravelReport(allFrames);
		
		return travelReport;
	}
}

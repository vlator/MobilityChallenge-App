package com.pels.mobilitychallenge.base.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.pels.mobilitychallenge.base.common.MalformedReading;

public class ReadingsLoader {

	private static final String ERR_MSG = "Specified Readings file could not be loaded.";
	private List<Reading> readings;
	BufferedReader reader;

	public ReadingsLoader(File inputFile) throws FileNotFoundException {
		if (!inputFile.exists() || inputFile.isDirectory()) {
			throw new FileNotFoundException(ERR_MSG);
		}
		readings = null;
		reader = new BufferedReader(new FileReader(inputFile));
	}

	public List<Reading> getAllReadings() {
		if (readings == null) {
			loadReadings();
		}
		return readings;
	}

	private void loadReadings() {
		String line;
		readings = new ArrayList<Reading>();
		int readingsCount = 0;
		try {
			reader.readLine(); // Gets rid of header
			while ((line = reader.readLine()) != null) {
				Reading reading = new Reading(line);
				readingsCount++;
				readings.add(reading);
			}
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedReading e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Total Readings in File: " + readingsCount);
	}

	public List<Reading> filterByLabel(Label label) {
		if (readings == null) {
			loadReadings();
		}
		
		List<Reading> filtered = new ArrayList<Reading>();
		for (Reading r : readings) {
			if (r.label == label) {
				filtered.add(r);
			}
		}
		return filtered;
	}

}

package com.pels.mobilitychallenge.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import com.pels.mobilitychallenge.app.common.Session;
import com.pels.mobilitychallenge.base.data.FramedReadings;
import com.pels.mobilitychallenge.base.data.Label;

public class TravelReport {
	private static final String BIKE_TRACKS = "Bike Tracks";

	private static final String OTHER_TRACKS = "Other Tracks";

	private static final String ALL_TRACKS = "All Tracks";

	private static final String TOTAL_GPS_COUNT = "Total GPS Count: ";

	private static final String TOTAL_DISTANCE_COVERED = "Total Distance Covered: ";

	private static final String AVERAGE_SPEED = "Average Speed: ";

	private static final String TOP_SPEED = "Top Speed: ";

	private static final String SECTION_SEP = "===============";

	private static final int MAX_TOLERANCE = 2;

	private static PrintWriter writer;

	private Date reportDate;
	int bikeTracksCount;
	int bikeTracksGpsCount = 0;
	double bikeTracksDistance = 0;
	double bikeTracksAvgSpeed = 0;
	double bikeTracksTopSpeed = -1;
	double bikeTracksDuration = 0;

	int otherTracksCount;
	int otherTracksGpsCount = 0;
	double otherTracksDistance = 0;
	double otherTracksAvgSpeed = 0;
	double otherTracksTopSpeed = -1;
	double otherTracksDuration = 0;
	// // double startTime;
	// // double endTime;

	private List<Track> bikeTracks;
	private List<Track> otherTracks;

	public TravelReport(List<Track> bikeTracks, List<Track> otherTracks) {
		this.bikeTracks = bikeTracks;
		bikeTracksCount = bikeTracks.size();

		for (Track t : bikeTracks) {
			bikeTracksAvgSpeed += t.getAvgSpeed();
			bikeTracksTopSpeed = bikeTracksTopSpeed > t.getTopSpeed() ? bikeTracksTopSpeed : t.getTopSpeed();
			bikeTracksDistance += t.getDistance();
			bikeTracksDuration += t.getDuration();
			bikeTracksGpsCount += t.getGpsCount();
		}
		bikeTracksAvgSpeed = bikeTracksAvgSpeed / bikeTracksCount;

		this.otherTracks = otherTracks;
		otherTracksCount = otherTracks.size();

		for (Track t : otherTracks) {
			otherTracksAvgSpeed += t.getAvgSpeed();
			otherTracksTopSpeed = otherTracksTopSpeed > t.getTopSpeed() ? otherTracksTopSpeed : t.getTopSpeed();
			otherTracksDistance += t.getDistance();
			otherTracksDuration += t.getDuration();
			otherTracksGpsCount += t.getGpsCount();
		}
		otherTracksAvgSpeed = otherTracksAvgSpeed / otherTracksCount;

		reportDate = Session.getDate();
	}

	protected TravelReport() {

	}

	public int getGpsCount(Label label) {
		if (label == Label.BIKE) {
			return bikeTracksGpsCount;
		} else if (label == Label.DEFAULT) {
			return otherTracksGpsCount;
		} else {
			return bikeTracksGpsCount + otherTracksGpsCount;
		}
	}

	public int getTracksCount(Label label) {
		if (label == Label.BIKE) {
			return bikeTracksCount;
		} else if (label == Label.DEFAULT) {
			return otherTracksCount;
		} else {
			return bikeTracksCount + otherTracksCount;
		}
	}

	public double getDistanceCovered(Label label) {
		if (label == Label.BIKE) {
			return bikeTracksDistance;
		} else if (label == Label.DEFAULT) {
			return otherTracksDistance;
		} else {
			return bikeTracksDistance + otherTracksDistance;
		}
	}

	public double getDuration(Label label) {
		if (label == Label.BIKE) {
			return bikeTracksDuration;
		} else if (label == Label.DEFAULT) {
			return otherTracksDuration;
		} else {
			return bikeTracksDuration + otherTracksDuration;
		}
	}

	public double getAverageSpeed(Label label) {
		if (label == Label.BIKE) {
			return bikeTracksAvgSpeed;
		} else if (label == Label.DEFAULT) {
			return otherTracksAvgSpeed;
		} else {
			return bikeTracksAvgSpeed + otherTracksAvgSpeed;
		}
	}

	public double getTopSpeed(Label label) {
		if (label == Label.BIKE) {
			return bikeTracksTopSpeed;
		} else if (label == Label.DEFAULT) {
			return otherTracksTopSpeed;
		} else {
			return bikeTracksTopSpeed + otherTracksTopSpeed;
		}
	}

	public List<Track> getTracks(Label label) {
		if (label == Label.BIKE) {
			return bikeTracks;
		} else
			return otherTracks;
	}

	// @Override
	// public String toString(){
	// StringBuilder sb = new StringBuilder();
	// sb.append("Bike: ")
	//
	// return sb.toString();
	// }

	public Date getDate() {
		return reportDate;
	}

	protected void setTopSpeed(Label label, double value) {
		if (label == Label.BIKE) {
			this.bikeTracksTopSpeed = value;
		} else
			this.otherTracksTopSpeed = value;
	}

	protected void setAverageSpeed(Label label, double value) {
		if (label == Label.BIKE) {
			this.bikeTracksAvgSpeed = value;
		} else
			this.otherTracksAvgSpeed = value;
	}

	protected void setDistance(Label label, double value) {
		if (label == Label.BIKE) {
			this.bikeTracksDistance = value;
		} else
			this.otherTracksDistance = value;
	}

	protected void setGPSCount(Label label, int value) {
		if (label == Label.BIKE) {
			this.bikeTracksGpsCount = value;
		} else
			this.otherTracksGpsCount = value;
	}

	protected void setTracksCount(Label label, int value) {
		if (label == Label.BIKE) {
			this.bikeTracksCount = value;
		} else
			this.otherTracksCount = value;
	}

	public static TravelReport extractTravelReport(List<FramedReadings> allFrames) {
		if (allFrames != null && !allFrames.isEmpty()) {

			List<Track> bikeTracks = new ArrayList<Track>();
			List<Track> otherTracks = new ArrayList<Track>();

			Label currentLabel = allFrames.get(0).getLabel();
			List<FramedReadings> frames = new ArrayList<FramedReadings>();

			int tolerance = 0;
			for (int i = 0; i < allFrames.size(); i++) {
				FramedReadings cFrame = allFrames.get(i);
				Label label = cFrame.getLabel();
				if (currentLabel == label) {
					frames.add(cFrame);
					tolerance = 0;
				} else {
					tolerance++;
					if (tolerance > MAX_TOLERANCE) {
						FramedReadings f1 = frames.remove(i - 1);
						FramedReadings f2 = frames.remove(i - 2);
						Track t = new Track(frames, currentLabel);
						if (t.getLabel() == Label.BIKE) {
							bikeTracks.add(t);
						} else {
							otherTracks.add(t);
						}

						frames.clear();
						frames.add(f2);
						frames.add(f1);
						tolerance = 0;
					}
				}
			}
			TravelReport report = new TravelReport(bikeTracks, otherTracks);
			return report;
		} else {
			return null;
		}
	}

	public static void writeToFile(File reportFile, TravelReport report) {
		Label[] labels = { Label.BIKE, Label.DEFAULT, null };
		try {
			writer = new PrintWriter(reportFile);
			for (Label l : labels) {
				String name;
				if (l == Label.DEFAULT) {
					name = OTHER_TRACKS;
				} else if (l == Label.BIKE) {
					name = BIKE_TRACKS;
				} else {
					name = ALL_TRACKS;
				}

				writer.println(name + " :" + report.getTracksCount(l));
				writer.println(TOTAL_GPS_COUNT + report.getGpsCount(l));
				writer.println(TOTAL_DISTANCE_COVERED + report.getDistanceCovered(l));
				writer.println(AVERAGE_SPEED + report.getAverageSpeed(l));
				writer.println(TOP_SPEED + report.getTopSpeed(l));
				writer.println(SECTION_SEP);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static TravelReport parse(File oldReportFile) {
		Scanner s1;
		TravelReport oldReport = null;
		try {
			s1 = new Scanner(oldReportFile);
			s1.useDelimiter(SECTION_SEP);

			while (s1.hasNext()) {
				oldReport = new TravelReport();
				String section = s1.next();
				parseSection(Label.BIKE, oldReport, section);
				section = s1.next();
				parseSection(Label.DEFAULT, oldReport, section);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}catch(NoSuchElementException e){
			e.printStackTrace();
			return null;
		}
		return oldReport;
	}

	private static void parseSection(Label label, TravelReport report, String section) throws NoSuchElementException {
		Scanner s2 = new Scanner(section);
		s2.useDelimiter(":");
		if (s2.hasNext()) {
			s2.next();
			report.setTracksCount(label, s2.nextInt());
			s2.next();
			report.setGPSCount(label, s2.nextInt());
			s2.next();
			report.setDistance(label, s2.nextDouble());
			s2.next();
			report.setAverageSpeed(label, s2.nextDouble());
			s2.next();
			report.setAverageSpeed(label, s2.nextDouble());			
		}
	}
}

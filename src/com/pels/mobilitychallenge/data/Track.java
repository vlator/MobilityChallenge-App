package com.pels.mobilitychallenge.data;

import java.util.List;

import com.pels.mobilitychallenge.base.data.FeatureVector;
import com.pels.mobilitychallenge.base.data.FramedReadings;
import com.pels.mobilitychallenge.base.data.Label;

public class Track {
	int gpsCount;
	double distance;
	double avgSpeed;
	double topSpeed;
	double duration;
	double startTime;
	double endTime;
	Label label;

	public Track(List<FramedReadings> frames, Label label) {
		this.label = label;

		for (FramedReadings f : frames) {
			FeatureVector t = f.getFeatureVector();
			avgSpeed += t.getAvgSpeed();
			topSpeed = topSpeed > t.getTopSpeed() ? topSpeed : t.getTopSpeed();
			distance += t.getDistance();
			duration += t.getDuration();
			gpsCount += f.getFrames().size();
		}
		
		avgSpeed = avgSpeed / frames.size();
	}

	/**
	 * @return the gpsCount
	 */
	public int getGpsCount() {
		return gpsCount;
	}

	/**
	 * @return the distance
	 */
	public double getDistance() {
		return distance;
	}

	/**
	 * @return the avgSpeed
	 */
	public double getAvgSpeed() {
		return avgSpeed;
	}

	/**
	 * @return the topSpeed
	 */
	public double getTopSpeed() {
		return topSpeed;
	}

	/**
	 * @return the duration in seconds
	 */
	public double getDuration() {
		return duration;
	}

	/**
	 * @return the startTime
	 */
	public double getStartTime() {
		return startTime;
	}

	/**
	 * @return the label
	 */
	public Label getLabel() {
		return label;
	}

	/**
	 * @return the endTime
	 */
	public double getEndTime() {
		return endTime;
	}

}

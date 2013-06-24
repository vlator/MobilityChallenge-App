package com.pels.mobilitychallenge.base.data;

import java.util.List;


public class FramedReadings {
	public static final String HEADER = FeatureVector.HEADER + ", Label";
	FeatureVector fv;
	List<Reading> frames;
	Label label;

	public FramedReadings(List<Reading> frames) {
		if (frames != null && frames.size() > 1) {
			this.frames = frames;
			fv = new FeatureVector(frames);
			label = null;
		}else{
			System.out.println("FramedReadaings err");
		}
	}

	public FramedReadings(List<Reading> frames, Label label) {
		this(frames);
		this.label = label;
	}

	public void setLabel(Label label) {
		this.label = label;
	}

	public Label getLabel() {
		return label;
	}

	public FeatureVector getFeatureVector() {
		return fv;
	}

	public List<Reading> getFrames() {
		return frames;
	}

	@Override
	public String toString() {
		return fv.toString() + "," + label;
	}
}

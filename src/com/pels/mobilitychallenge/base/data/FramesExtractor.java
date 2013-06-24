package com.pels.mobilitychallenge.base.data;

import java.util.ArrayList;
import java.util.List;


public class FramesExtractor {

	private int windowLength;

	public FramesExtractor(int windowLength) {
		this.windowLength = windowLength;
	}

	public List<FramedReadings> getFramedReadings(List<Reading> allFrames) {
		List<FramedReadings> framedReadings = new ArrayList<FramedReadings>();
		if (!allFrames.isEmpty()) {
			int i = 0;

			while (i < allFrames.size()) {
				List<Reading> frame = getNextFrame(i, allFrames);
				i += frame.size();
				FramedReadings framed = new FramedReadings(frame);
				if (framed != null) {
					framedReadings.add(framed);
				}
			}
		}

		return framedReadings;
	}

	private List<Reading> getNextFrame(int startIndex, List<Reading> allFrames) {
		List<Reading> nextFrame = new ArrayList<Reading>();
		double endTime = allFrames.get(startIndex).time + (windowLength * 1000);
		for (int i = startIndex; i < allFrames.size(); i++) {
			double cTime = allFrames.get(i).time;
			if (cTime <= endTime) {
				nextFrame.add(allFrames.get(i));
			} else {
				break;
			}
		}
		return nextFrame;
	}
}

package com.pels.mobilitychallenge.app;

import com.pels.mobilitychallenge.base.data.FeatureVector;
import com.pels.mobilitychallenge.base.data.Label;

/**
 * Decision model, trained with data set of 500 entries of feature vectors. Each
 * feature vector was computed on a frame of window length of 20secs.
 * 
 * Accuracy of around 77%.
 * 
 * @author cpels
 * 
 */
public class SimpleClassifier implements Classifier {

	@Override
	public Label classifier(FeatureVector fv) {
		double avgSpeed = fv.getAvgSpeed();
		double topSpeed = fv.getTopSpeed();

		if (avgSpeed <= 2.181079) {
			return Label.DEFAULT;
		} else {
			if (avgSpeed <= 5.580337) {
				return Label.BIKE;
			} else {
				if (topSpeed <= 6.56087) {
					return Label.BIKE;
				} else {
					return Label.DEFAULT;
				}
			}
		}
	}
}

package com.pels.mobilitychallenge.app;

import com.pels.mobilitychallenge.base.data.FeatureVector;
import com.pels.mobilitychallenge.base.data.Label;

public class SimpleClassifier implements Classifier {

	@Override
	public Label classifier(FeatureVector fv) {
		// TODO Auto-generated method stub
		return Label.BIKE;
	}

}

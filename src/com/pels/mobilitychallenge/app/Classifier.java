package com.pels.mobilitychallenge.app;

import com.pels.mobilitychallenge.base.data.FeatureVector;
import com.pels.mobilitychallenge.base.data.Label;

public interface Classifier {
	public Label classifier(FeatureVector fv);
}

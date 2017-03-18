package com.untrackr.alerter.alert;

import org.apache.commons.math3.util.Pair;

import java.util.ArrayList;

public class AlertData extends ArrayList<Pair<String, String>> {

	public AlertData() {
	}

	public void add(String key, String value) {
		add(new Pair<>(key, value));
	}

}

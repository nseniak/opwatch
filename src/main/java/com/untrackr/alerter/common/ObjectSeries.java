package com.untrackr.alerter.common;

import com.untrackr.alerter.processor.common.AlerterException;
import com.untrackr.alerter.processor.common.ExceptionContext;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// TODO Make this a JavaScript function; make producers output arrays instead
public class ObjectSeries extends ArrayList<SeriesObject> {

	private DescriptiveStatistics stats;
	private SimpleRegression regression;

	public ObjectSeries() {
	}

	public ObjectSeries(Collection<SeriesObject> collection) {
		super(collection);
	}

	public SimpleRegression getRegression() {
		if (regression == null) {
			regression = new SimpleRegression(true);
			for (SeriesObject object : this) {
				regression.addData(object.getStamp(), number(object.getValue()));
			}
		}
		return regression;
	}

	public DescriptiveStatistics getStats() {
		if (stats == null) {
			stats = new DescriptiveStatistics();
			for (SeriesObject object : this) {
				stats.addValue(number(object.getValue()));
			}
		}
		return stats;
	}

	public List<Object> getValues() {
		List<Object> result = new ArrayList<>();
		for (SeriesObject object : this) {
			result.add(object.getValue());
		}
		return result;
	}

	private double number(Object object) {
		if (object instanceof Number) {
			return ((Number) object).doubleValue();
		} else {
			throw new AlerterException("illegal operation on a non-number", ExceptionContext.makeToplevel());
		}
	}

}

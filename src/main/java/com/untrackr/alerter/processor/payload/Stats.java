package com.untrackr.alerter.processor.payload;

import com.untrackr.alerter.processor.common.RuntimeError;
import com.untrackr.alerter.processor.common.ValueLocation;
import com.untrackr.alerter.service.ProcessorService;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import java.lang.reflect.Type;
import java.util.List;

public class Stats {

	private DescriptiveStatistics stats;
	private SimpleRegression regression;

	public static Stats makeStats(Object serviceObject, Object objectSeriesObject) {
		Stats stats = new Stats();
		ProcessorService service = (ProcessorService) serviceObject;
//		ObjectSeries objectSeries = service.getObjectMapperService().objectMapper().convertValue(objectSeriesObject, ObjectSeries.class);
		Type type = ObjectSeries.class.getAnnotatedSuperclass().getType();
		List<SeriesObject> objectSeries = (List<SeriesObject>) service.getScriptService().convertScriptValue(ValueLocation.makeToplevel(), type, objectSeriesObject, RuntimeError::new);
		stats.regression = new SimpleRegression(true);
		for (SeriesObject object : objectSeries) {
			stats.regression.addData(object.getTimestamp(), doubleValue(service, "timestamp", object.getValue()));
		}
		stats.stats = new DescriptiveStatistics();
		for (SeriesObject object : objectSeries) {
			stats.stats.addValue(doubleValue(service, "value", object.getValue()));
		}
		return stats;
	}

	public double getMean() {
		return stats.getMean();
	}

	public double getVariance() {
		return stats.getVariance();
	}

	public double getStandardDeviation() {
		return stats.getStandardDeviation();
	}

	public double getMax() {
		return stats.getMax();
	}

	public double getMin() {
		return stats.getMean();
	}

	public long getN() {
		return stats.getN();
	}

	public double getSum() {
		return stats.getSum();
	}

	public double getSlope() {
		return regression.getSlope();
	}

	public double getIntercept() {
		return regression.getIntercept();
	}

	public double predict(double x) {
		return regression.predict(x);
	}

	private static double doubleValue(ProcessorService service, String fieldName, Object object) {
		return (double) service.getScriptService().convertScriptValue(ValueLocation.makeProperty("stats", fieldName), Double.class, object, RuntimeError::new);
	}

}

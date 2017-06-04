package org.opwatch.processor.payload;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.opwatch.processor.common.RuntimeError;
import org.opwatch.processor.common.ValueLocation;
import org.opwatch.service.ProcessorService;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Stats extends PayloadPojoValue {

	private DescriptiveStatistics stats;
	private SimpleRegression regression;

	public static Stats makeStats(Object serviceObject, Object objectSeriesArray) {
		Stats stats = new Stats();
		ProcessorService service = (ProcessorService) serviceObject;
		Type type = SeriesObjectList.class.getAnnotatedSuperclass().getType();
		List<SeriesObject> objectSeries = (List<SeriesObject>) service.getScriptService().convertScriptValue(ValueLocation.makeToplevel(), type, objectSeriesArray, RuntimeError::new);
		stats.regression = new SimpleRegression(true);
		for (SeriesObject object : objectSeries) {
			stats.regression.addData(object.getTimestamp(), doubleValue(service, "value", object.getValue()));
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

	private static double doubleValue(ProcessorService service, String fieldName, Object object) {
		return (double) service.getScriptService().convertScriptValue(ValueLocation.makeProperty("stats", fieldName), Double.class, object, RuntimeError::new);
	}

	public abstract static class SeriesObjectList extends ArrayList<SeriesObject> {

	}

}

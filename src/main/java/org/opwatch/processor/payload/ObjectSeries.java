package org.opwatch.processor.payload;

import java.util.ArrayList;
import java.util.Collection;

public class ObjectSeries extends ArrayList<SeriesObject> {

	public ObjectSeries() {
	}

	public ObjectSeries(Collection<SeriesObject> queue) {
		super(queue);
	}

}

package org.opwatch.processor.payload;

import org.opwatch.service.ScriptService;

public class ObjectSeries {

	public static Object toJavascript(ScriptService scriptService, SeriesObject[] array) {
		return scriptService.array(array);
	}

}

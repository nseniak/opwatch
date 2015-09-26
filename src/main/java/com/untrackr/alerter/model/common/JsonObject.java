package com.untrackr.alerter.model.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonObject extends HashMap<String, Object> {

	public static JsonObject deepCopy(JsonObject object) {
		return (JsonObject) deepCopyRec(object);
	}

	private static Object deepCopyRec(Object object) {
		if (object instanceof Map) {
			JsonObject copy = new JsonObject();
			for (Map.Entry<Object, Object> entry : ((Map<Object, Object>) object).entrySet()) {
				Object key = entry.getKey();
				if (!(key instanceof String)) {
					throw new IllegalStateException("invalid Json object");
				}
				copy.put((String) key, deepCopyRec(entry.getValue()));
			}
			return copy;
		} else if (object instanceof List) {
			List<Object> copy = new ArrayList<>();
			for (Object element : (List) object) {
				copy.add(deepCopyRec(element));
			}
			return copy;
		} else {
			return object;
		}
	}

}

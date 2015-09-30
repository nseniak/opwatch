package com.untrackr.alerter.model.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonUtil {

	public static Object deepCopy(Object object) {
		if (object instanceof Map) {
			HashMap copy = new HashMap();
			for (Map.Entry entry : ((Map<Object, Object>) object).entrySet()) {
				Object key = entry.getKey();
				if (!(key instanceof String)) {
					throw new IllegalStateException("invalid Json object");
				}
				copy.put(key, deepCopy(entry.getValue()));
			}
			return copy;
		} else if (object instanceof List) {
			List<Object> copy = new ArrayList<>();
			for (Object element : (List) object) {
				copy.add(deepCopy(element));
			}
			return copy;
		} else {
			return object;
		}
	}

}

package org.opwatch.processor.payload;

import jdk.nashorn.internal.runtime.ScriptRuntime;
import org.springframework.http.HttpHeaders;

import java.util.List;
import java.util.Objects;

public class PayloadHeadersValue extends PayloadMapValue<HttpHeaders, List<String>> {

	public PayloadHeadersValue(HttpHeaders headers) {
		super(headers);
	}

	@Override
	public Object getMember(String name) {
		Objects.requireNonNull(name);
		if (map.containsKey(name)) {
			List<String> values = map.get(name);
			if (values.size() == 1) {
				return values.get(0);
			} else {
				return new PayloadArrayValue<>(values);
			}
		} else {
			return ScriptRuntime.UNDEFINED;
		}
	}

}

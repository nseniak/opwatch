package org.opwatch.processor.payload;

import jdk.nashorn.internal.runtime.ScriptRuntime;
import org.opwatch.service.ScriptService;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class PayloadMapValue<M extends Map<String, T>, T> extends PayloadScriptValue {

	protected M map;

	public PayloadMapValue(M map) {
		this.map = map;
	}

	@Override
	public Object getSlot(int index) {
		return ScriptRuntime.UNDEFINED;
	}

	@Override
	public Object getMember(String name) {
		Objects.requireNonNull(name);
		if (map.containsKey(name)) {
			return map.get(name);
		} else {
			return ScriptRuntime.UNDEFINED;
		}
	}

	@Override
	public Set<String> keySet() {
		return map.keySet();
	}

}

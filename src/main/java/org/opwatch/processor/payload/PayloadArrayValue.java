package org.opwatch.processor.payload;

import jdk.nashorn.internal.runtime.ScriptRuntime;
import org.opwatch.service.ScriptService;

import java.util.List;
import java.util.Objects;

public class PayloadArrayValue<T> extends PayloadScriptValue {

	private List<T> list;

	public PayloadArrayValue(List<T> list) {
		this.list = list;
	}

	@Override
	public Object getSlot(int index) {
		if (hasSlot(index)) {
			return list.get(index);
		} else {
			return ScriptRuntime.UNDEFINED;
		}
	}

	@Override
	public Object getMember(String name) {
		Objects.requireNonNull(name);
		if (name.equals("length")) {
			return list.size();
		} else if (name.equals("__payloadArray")) {
			return true;
		} else {
			return ScriptRuntime.UNDEFINED;
		}
	}

	@Override
	public boolean hasSlot(int slot) {
		return ((slot >= 0) && (slot < list.size()));
	}

	@Override
	public boolean isArray() {
		return true;
	}

}

package org.opwatch.processor.payload;

import jdk.nashorn.internal.runtime.ScriptRuntime;
import org.opwatch.service.ScriptService;

import java.util.Objects;

public class PayloadArrayValue<T> extends PayloadScriptValue {

	private T[] array;

	public PayloadArrayValue(T[] array) {
		this.array = array;
	}

	@Override
	public Object toJavascript(ScriptService scriptService) {
		return this;
	}

	@Override
	public Object getSlot(int index) {
		if (hasSlot(index)) {
			return array[index];
		} else {
			return ScriptRuntime.UNDEFINED;
		}
	}

	@Override
	public Object getMember(String name) {
		Objects.requireNonNull(name);
		if (name.equals("length")) {
			return array.length;
		} else if (name.equals("__payloadArray")) {
			return true;
		} else {
			return ScriptRuntime.UNDEFINED;
		}
	}

	@Override
	public boolean hasSlot(int slot) {
		return ((slot >= 0) && (slot < array.length));
	}

	@Override
	public boolean isArray() {
		return true;
	}

}

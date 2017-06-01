package org.opwatch.processor.config;

import java.util.List;

public class ValueOrList<T> extends ConfigPropertyValue {

	public enum Type {
		value, list
	}

	private Type type;
	private T value;
	private List<T> list;

	private ValueOrList() {
	}

	public static <T> ValueOrList<T> makeValue(T value) {
		ValueOrList<T> vol = new ValueOrList<>();
		vol.type = Type.value;
		vol.value = value;
		return vol;
	}

	public static <T> ValueOrList makeList(List<T> list) {
		ValueOrList<T> vol = new ValueOrList<>();
		vol.type = Type.list;
		vol.list = list;
		return vol;
	}

	public Type getType() {
		return type;
	}

	public T getValue() {
		return value;
	}

	public List<T> getList() {
		return list;
	}

}

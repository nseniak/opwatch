package com.untrackr.alerter.processor.common;

public class ConstructionContext {

	/**
	 * Name of the processor
	 */
	private String name;
	/**
	 * Script stack captured when the processor was built
	 */
	private ScriptStack stack;

	public ConstructionContext(String name, ScriptStack stack) {
		this.name = name;
		this.stack = stack;
	}

	public String descriptor() {
		StringBuilder builder = new StringBuilder();
		builder.append(getName());
		if (!stack.empty()) {
			builder.append(" built ").append(stack.asString());
		}
		return builder.toString();
	}

	public String getName() {
		return name;
	}

}

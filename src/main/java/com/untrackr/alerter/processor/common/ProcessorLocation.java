package com.untrackr.alerter.processor.common;

public class ProcessorLocation {

	/**
	 * Name of the processor
	 */
	private String name;
	/**
	 * Stack when the processor was built; empty if it was built by code
	 */
	private ScriptStack stack;

	public ProcessorLocation(String name) {
		this.name = name;
		this.stack = ScriptStack.currentStack();
	}

	public String descriptor() {
		StringBuilder builder = new StringBuilder();
		builder.append(getName());
		if (!stack.empty()) {
			builder.append(" processor built at ").append(stack.asString());
		}
		return builder.toString();
	}

	public String getName() {
		return name;
	}

}

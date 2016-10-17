package com.untrackr.alerter.processor.common;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class ScriptStack {

	private List<ScriptStackElement> elements = new ArrayList<>();

	private ScriptStack() {
	}

	private ScriptStack(Throwable t) {
		initFromStack(t.getStackTrace());
	}

	private ScriptStack(StackTraceElement[] stack) {
		initFromStack(stack);
	}

	public static ScriptStack exceptionStack(Throwable t) {
		return new ScriptStack(t);
	}

	public static ScriptStack currentStack() {
		return new ScriptStack(Thread.currentThread().getStackTrace());
	}

	public static ScriptStack emptyStack() {
		return new ScriptStack();
	}

	private void initFromStack(StackTraceElement[] javaStack) {
		ScriptStack stack = new ScriptStack();
		for (StackTraceElement element : javaStack) {
			if (element.getMethodName().equals(":program")) {
				addElement(element.getFileName(), element.getLineNumber());
			}
		}
	}

	public void addElement(String fileName, int lineNumber) {
		if (fileName != null) {
			elements.add(new ScriptStackElement(fileName, lineNumber));
		}
	}

	public ScriptStackElement top() {
		if (elements.isEmpty()) {
			return null;
		} else {
			return elements.get(elements.size() - 1);
		}
	}

	public boolean empty() {
		return elements.isEmpty();
	}

	public String asString() {
		StringJoiner joiner = new StringJoiner(" > ");
		Lists.reverse(elements).forEach(element -> joiner.add(element.getFileName() + ":" + element.getLineNumber()));
		return joiner.toString();
	}

	public static class ScriptStackElement {

		private String fileName;
		private int lineNumber;

		public ScriptStackElement(String fileName, int lineNumber) {
			this.fileName = fileName;
			this.lineNumber = lineNumber;
		}

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

		public int getLineNumber() {
			return lineNumber;
		}

		public void setLineNumber(int lineNumber) {
			this.lineNumber = lineNumber;
		}

	}

}
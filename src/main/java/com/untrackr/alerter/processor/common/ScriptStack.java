package com.untrackr.alerter.processor.common;

import com.google.common.collect.Lists;
import jdk.nashorn.api.scripting.NashornException;

import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class ScriptStack {

	private List<ScriptStackElement> elements = new ArrayList<>();

	private ScriptStack() {
	}

	private ScriptStack(Throwable t) {
		Throwable cause = t;
		while (cause.getCause() != null) {
			cause = cause.getCause();
		}
		initFromStack(cause.getStackTrace());
	}

	private ScriptStack(StackTraceElement[] stack) {
		initFromStack(stack);
	}

	public static ScriptStack exceptionStack(Throwable cause, String fileName, int lineNumber) {
		ScriptStack scriptStack = new ScriptStack(cause);
		if (fileName != null) {
			ScriptStack.ScriptStackElement top = scriptStack.top();
			if ((top == null) || !(fileName.equals(top.getFileName()) && (lineNumber == top.getLineNumber()))) {
				scriptStack.addElement(fileName, lineNumber);
			}
		}
		return scriptStack;
	}

	public static ScriptStack exceptionStack(ScriptException cause) {
		return exceptionStack(cause, cause.getFileName(), cause.getLineNumber());
	}

	public static ScriptStack exceptionStack(NashornException cause) {
		return exceptionStack(cause, cause.getFileName(), cause.getLineNumber());
	}

	public static ScriptStack currentStack() {
		return new ScriptStack(Thread.currentThread().getStackTrace());
	}

	public static ScriptStack emptyStack() {
		return new ScriptStack();
	}

	private void initFromStack(StackTraceElement[] javaStack) {
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

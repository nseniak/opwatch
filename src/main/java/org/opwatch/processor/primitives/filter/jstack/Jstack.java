package org.opwatch.processor.primitives.filter.jstack;

import jdk.nashorn.internal.objects.NativeRegExp;
import org.opwatch.common.Assertion;
import org.opwatch.processor.common.ProcessorPayloadExecutionScope;
import org.opwatch.processor.common.RuntimeError;
import org.opwatch.processor.payload.Payload;
import org.opwatch.processor.payload.PayloadObjectValue;
import org.opwatch.processor.primitives.filter.Filter;
import org.opwatch.service.ProcessorService;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Jstack extends Filter<JstackConfig> {

	private NativeRegExp methodRegexp;
	private ParsingState state = new ParsingState();

	private final static int MAX_EXCEPTION_LINES = 1000;

	public Jstack(ProcessorService processorService, JstackConfig configuration, String name, NativeRegExp methodRegexp) {
		super(processorService, configuration, name);
		this.methodRegexp = methodRegexp;
	}

	@Override
	public synchronized void consume(Payload<?> input) {
		String text = payloadValue(input, String.class);
		String[] lines = text.split("\n");
		for (String line : lines) {
			ParsedException exception = parseNextLine(line, input);
			if (exception != null) {
				outputTransformed(exception, input);
				return;
			}
		}
	}

	private static String fullyQualifiedIdentifierRegex = "(\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*\\.)+\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*(\\.<init>)?";
	private static Pattern exceptionDescription = Pattern.compile("(?<name>" + fullyQualifiedIdentifierRegex + "): (?<message>.*)$");
	private static Pattern exceptionLinePattern = Pattern.compile("^" + exceptionDescription);
	private static Pattern atLinePattern = Pattern.compile("^\\p{Space}+at (?<method>" + fullyQualifiedIdentifierRegex + ")\\((?<location>[^)]*)\\).*");
	private static Pattern causedByPattern = Pattern.compile("^(?:Caused by:|Suppressed:) " + exceptionDescription);
	private static Pattern omittedPattern = Pattern.compile("^\\p{Space}+\\.{3} \\p{N}+ (?:more|common frames omitted)");
	private static Pattern blankLine = Pattern.compile("^\\p{Space}*$");

	public ParsedException parseNextLine(String line, Payload<?> input) {
		String latestNonBlank = state.getLatestNonBlankLine();
		if (!blankLine.matcher(line).matches()) {
			state.setLatestNonBlankLine(line);
		}
		ParsedException currentException = state.getException();
		if (currentException == null) {
			// We're outside of an exception stack trace. Look for an exception line.
			Matcher exceptionLineMatcher = exceptionLinePattern.matcher(line);
			if (exceptionLineMatcher.matches()) {
				// Set current exception
				String name = exceptionLineMatcher.group("name");
				String message = exceptionLineMatcher.group("message");
				String previous = (latestNonBlank != null) ? latestNonBlank : "";
				ParsedException exception = new ParsedException(name, message, previous);
				exception.addLine(line);
				state.setInMessage(true);
				state.setLinesSinceException(0);
				state.setException(exception);
			}
			return null;
		}
		// We're inside an exception stack trace.
		state.setLinesSinceException(state.getLinesSinceException() + 1);
		if (state.getLinesSinceException() > MAX_EXCEPTION_LINES) {
			// This exception trace is suspiciously long. Most probably a parsing error.
			if (currentException.getMethod() == null) {
				// Set default value for method and location.
				currentException.setMethod("<unknown method>");
				currentException.setLocation("<unknown location>");
			}
			processorService.signalSystemException(new RuntimeError("Cannot parse exception trace longer than " + MAX_EXCEPTION_LINES + " lines",
					new ProcessorPayloadExecutionScope(this, input)));
			state.reset();
			return currentException;
		}
		// We're after an exception line. Check if it's an "at" line.
		Matcher atLineMatcher = atLinePattern.matcher(line);
		if (atLineMatcher.matches()) {
			state.setInMessage(false);
			currentException.addLine(line);
			// Set the method and location, if not already set
			String method = atLineMatcher.group("method");
			String location = atLineMatcher.group("location");
			String currentMethod = currentException.getMethod();
			if ((currentMethod == null)
					|| ((methodRegexp != null) && !methodRegexp.test(currentMethod) && methodRegexp.test(method))) {
				currentException.setMethod(method);
				currentException.setLocation(location);
			}
			return null;
		}
		Matcher causedByMatcher = causedByPattern.matcher(line);
		if (causedByMatcher.matches()) {
			state.setInMessage(true);
			currentException.addLine(line);
			return null;
		}
		Matcher omittedMatcher = omittedPattern.matcher(line);
		if (omittedMatcher.matches()) {
			state.setInMessage(false);
			currentException.addLine(line);
			return null;
		}
		if (state.isInMessage()) {
			// We're in a multiline exception message.
			currentException.setExceptionMessage(currentException.getExceptionMessage() + "\n" + line);
			currentException.addLine(line);
			return null;
		}
		// First line after an exception
		state.reset();
		ParsedException newException = parseNextLine(line, input);
		Assertion.assertExecutionState(newException == null);
		return currentException;
	}

	static class ParsingState {

		/**
		 * Set when the exception line was recognized
		 */
		private ParsedException exception;
		/**
		 * Number of lines read after since the exception field was set.
		 */
		private int linesSinceException;
		/**
		 * True if at least one "at" line has been spotted since the exception line
		 */
		private boolean inMessage;
		/**
		 * Last non-blank line seen.
		 */
		private String latestNonBlankLine;

		public ParsingState() {
			reset();
		}

		public void reset() {
			this.exception = null;
			this.linesSinceException = 0;
			this.inMessage = false;
			this.latestNonBlankLine = null;
		}

		public ParsedException getException() {
			return exception;
		}

		public void setException(ParsedException exception) {
			this.exception = exception;
		}

		public int getLinesSinceException() {
			return linesSinceException;
		}

		public void setLinesSinceException(int linesSinceException) {
			this.linesSinceException = linesSinceException;
		}

		public boolean isInMessage() {
			return inMessage;
		}

		public void setInMessage(boolean inMessage) {
			this.inMessage = inMessage;
		}

		public String getLatestNonBlankLine() {
			return latestNonBlankLine;
		}

		public void setLatestNonBlankLine(String latestNonBlankLine) {
			this.latestNonBlankLine = latestNonBlankLine;
		}

	}

	public static class ParsedException extends PayloadObjectValue {

		private String exceptionClass;
		private String exceptionMessage;
		private String method;
		private String location;
		private String previousLine;
		private List<String> stack;

		private ParsedException() {
		}

		public ParsedException(String exceptionClass, String exceptionMessage, String previousLine) {
			this.exceptionClass = exceptionClass;
			this.exceptionMessage = exceptionMessage;
			this.previousLine = previousLine;
			this.stack = new ArrayList<>();
		}

		private void addLine(String line) {
			stack.add(line);
		}

		public String getExceptionClass() {
			return exceptionClass;
		}

		public void setExceptionClass(String exceptionClass) {
			this.exceptionClass = exceptionClass;
		}

		public String getExceptionMessage() {
			return exceptionMessage;
		}

		public void setExceptionMessage(String exceptionMessage) {
			this.exceptionMessage = exceptionMessage;
		}

		public String getMethod() {
			return method;
		}

		public void setMethod(String method) {
			this.method = method;
		}

		public String getLocation() {
			return location;
		}

		public void setLocation(String location) {
			this.location = location;
		}

		public String getPreviousLine() {
			return previousLine;
		}

		public void setPreviousLine(String previousLine) {
			this.previousLine = previousLine;
		}

		public List<String> getStack() {
			return stack;
		}

		public void setStack(List<String> stack) {
			this.stack = stack;
		}

	}

}

package com.untrackr.alerter.processor.primitives.transformer.jstack;

import com.untrackr.alerter.alert.Alert;
import com.untrackr.alerter.processor.payload.Payload;
import com.untrackr.alerter.processor.payload.PayloadObjectValue;
import com.untrackr.alerter.processor.primitives.transformer.Transformer;
import com.untrackr.alerter.service.ProcessorService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Jstack extends Transformer<JstackDescriptor> {

	private String propertyName;
	private Pattern methodPattern;
	private ParsingState state = new ParsingState();

	private final static int MAX_EXCEPTION_LINES = 500;

	public Jstack(ProcessorService processorService, JstackDescriptor descriptor, String name, String propertyName, Pattern methodPattern) {
		super(processorService, descriptor, name);
		this.propertyName = propertyName;
		this.methodPattern = methodPattern;
	}

	@Override
	public void consume(Payload input) {
		String text = payloadPropertyValue(input, propertyName, String.class);
		String[] lines = text.split("\n");
		for (String line : lines) {
			ParsedException exception = parseNextLine(line);
			if (exception != null) {
				outputTransformed(exception, input);
				return;
			}
		}
	}

	private static String fullyQualifiedIdentifierRegex = "(\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*\\.)+\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*(\\.<init>)?";
	private static Pattern exceptionDescription = Pattern.compile("(?<name>" + fullyQualifiedIdentifierRegex + "): (?<message>.*)$");
	private static Pattern exceptionLinePattern = Pattern.compile("^" + exceptionDescription);
	private static Pattern atLinePattern = Pattern.compile("^\tat (?<method>" + fullyQualifiedIdentifierRegex + ")\\((?<location>[^)]*)\\).*");
	private static Pattern causedByPattern = Pattern.compile("^Caused by: " + exceptionDescription);
	private static Pattern blankLine = Pattern.compile("^\\p{Space}*$");

	public ParsedException parseNextLine(String line) {
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
				String previous = (state.getLatestNonBlankLine() != null) ? state.getLatestNonBlankLine() : "";
				ParsedException exception = new ParsedException(name, message, previous);
				state.setAtLineSeen(false);
				state.setLinesSinceException(0);
				state.setException(exception);
			}
			return null;
		}
		// We're inside an exception stack trace.
		state.setLinesSinceException(state.getLinesSinceException() + 1);
		// We're after an exception line. Check if it's an "at" line.
		Matcher atLineMatcher = atLinePattern.matcher(line);
		if (atLineMatcher.matches()) {
			// We're on a at line. Use it to set the method and location.
			state.setAtLineSeen(true);
			String method = atLineMatcher.group("method");
			String location = atLineMatcher.group("location");
			if ((methodPattern == null) || (methodPattern.matcher(method).find())) {
				// Found a satisfactory line in the stack. Return the exception.
				currentException.setMethod(method);
				currentException.setLocation(location);
				currentException.computeCombined();
				state.reset();
				return currentException;
			}
			if (currentException.getMethod() == null) {
				// Set default value for method and location.
				currentException.setMethod(method);
				currentException.setLocation(location);
			}
			return null;
		}
		Matcher causedByMatcher = causedByPattern.matcher(line);
		if (causedByMatcher.matches()) {
			// We're on a Caused by line.
			state.setAtLineSeen(false);
			return null;
		}
		if (state.isAtLineSeen()) {
			// We've successfully parsed the exception message and at least one "at" line, and now we find a line
			// that doesn't match a part of an exception trace. Means we've reached the end of the exception.
			currentException.computeCombined();
			state.reset();
			return currentException;
		}
		if (state.getLinesSinceException() > MAX_EXCEPTION_LINES) {
			// This exception trace is suspiciously long. Most probably a parsing error.
			if (currentException.getMethod() == null) {
				// Set default value for method and location.
				currentException.setMethod("<unknown method>");
				currentException.setLocation("<unknown location>");
			}
			currentException.computeCombined();
			processorService.infrastructureAlert(Alert.Priority.high, "Cannot parse exception trace", currentException.getCombined());
			state.reset();
			return currentException;
		}
		// We're in a multiline exception message, e.g:
		//
		//		com.google.api.client.googleapis.json.GoogleJsonResponseException: 500 Internal Server Error
		//		{
		//			"code" : 500,
		//				"errors" : [ {
		//			"domain" : "global",
		//					"message" : "Backend Error",
		//					"reason" : "backendError"
		//		} ],
		//			"message" : "Backend Error"
		//		}
		//		at com.google.api.client.googleapis.json.GoogleJsonResponseException.from(GoogleJsonResponseException.java:145) ~[google-api-client-1.20.0.jar!/:1.20.0]
		//		at com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest.newExceptionOnError(AbstractGoogleJsonClientRequest.java:113) ~[google-api-client-1.20.0.jar!/:1.20.0]
		//
		// Keep looking for an "at" line
		return null;
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
		private boolean atLineSeen;
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
			this.atLineSeen = false;
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

		public boolean isAtLineSeen() {
			return atLineSeen;
		}

		public void setAtLineSeen(boolean atLineSeen) {
			this.atLineSeen = atLineSeen;
		}

		public String getLatestNonBlankLine() {
			return latestNonBlankLine;
		}

		public void setLatestNonBlankLine(String latestNonBlankLine) {
			this.latestNonBlankLine = latestNonBlankLine;
		}

	}

	public static class ParsedException extends PayloadObjectValue {

		private String name;
		private String message;
		private String method;
		private String location;
		private String combined;
		private String previous;

		public ParsedException(String name, String message, String previous) {
			this.name = name;
			this.message = message;
			this.previous = previous;
		}

		private void computeCombined() {
			combined = name + "::" + message + "::" + ((method == null) ? "" : method);
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
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

		public String getCombined() {
			return combined;
		}

		public void setCombined(String combined) {
			this.combined = combined;
		}

		public String getPrevious() {
			return previous;
		}

		public void setPrevious(String previous) {
			this.previous = previous;
		}

	}

}

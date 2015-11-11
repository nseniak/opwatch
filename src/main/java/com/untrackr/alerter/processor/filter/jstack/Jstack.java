package com.untrackr.alerter.processor.filter.jstack;

import com.untrackr.alerter.common.ScriptObject;
import com.untrackr.alerter.processor.common.IncludePath;
import com.untrackr.alerter.processor.common.Payload;
import com.untrackr.alerter.processor.common.RuntimeProcessorError;
import com.untrackr.alerter.processor.filter.Filter;
import com.untrackr.alerter.service.ProcessorService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Jstack extends Filter {

	private String fieldName;
	private Pattern methodPattern;
	private boolean errorSignaled = false;
	private ParsingState state = new ParsingState();

	private final static int MAX_EXCEPTION_LINES = 50;

	public Jstack(ProcessorService processorService, IncludePath path, String fieldName, Pattern methodPattern) {
		super(processorService, path);
		this.fieldName = fieldName;
		this.methodPattern = methodPattern;
	}

	@Override
	public void consume(Payload input) {
		try {
			String text = payloadFieldValue(input, fieldName, String.class);
			String[] lines = text.split("\n");
			for (String line : lines) {
				ParsedException exception = parseNextLine(line);
				if (exception != null) {
					outputFiltered(exception, input);
					return;
				}
			}
		} catch (RuntimeProcessorError e) {
			if (!errorSignaled) {
				errorSignaled = true;
				throw e;
			}
		}
	}

	private static String fullyQualifiedIdentifierRegex = "(\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*\\.)+\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*";
	private static Pattern exceptionLinePattern = Pattern.compile("^(?<name>" + fullyQualifiedIdentifierRegex + "): (?<message>.*)$");
	private static Pattern atLinePattern = Pattern.compile("^\tat (?<method>" + fullyQualifiedIdentifierRegex + ")\\((?<location>[^)]*)\\).*");

	public ParsedException parseNextLine(String line) {
		if (state.getException() != null) {
			// We're after an exception line. Check if it's an "at" line.
			ParsedException exception = state.getException();
			Matcher matcher = atLinePattern.matcher(line);
			if (!matcher.matches()) {
				// We don't recognize an "at" line.
				if (state.getLinesSinceException() > MAX_EXCEPTION_LINES) {
					// Give up on finding the method and location. Return the exception as is.
					exception.computeCombined();
					state.reset();
					return exception;
				}
				// Keep looking for an "at" line
			} else {
				String method = matcher.group("method");
				String location = matcher.group("location");
				if ((methodPattern == null) || (methodPattern.matcher(method).find())) {
					// Found a satisfactory line in the stack. Return the exception.
					exception.setMethod(method);
					exception.setLocation(location);
					exception.computeCombined();
					state.reset();
					return exception;
				}
				if (exception.getMethod() == null) {
					// Set default value for method and location.
					exception.setMethod(method);
					exception.setLocation(location);
				}
				// Keep looking for a satisfactory line
			}
		}
		// Look for an exception line.
		Matcher matcher = exceptionLinePattern.matcher(line);
		if (matcher.matches()) {
			String name = matcher.group("name");
			String message = matcher.group("message");
			ParsedException exception = new ParsedException(processorService, name, message);
			state.setException(exception);
		}
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

		public ParsingState() {
			reset();
		}

		public void reset() {
			this.exception = null;
			this.linesSinceException = 0;
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

	}

	public static class ParsedException extends ScriptObject {

		private String name;
		private String message;
		private String method;
		private String location;
		private String combined;

		public ParsedException(ProcessorService processorService, String name, String message) {
			super(processorService);
			this.name = name;
			this.message = message;
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

	}

}

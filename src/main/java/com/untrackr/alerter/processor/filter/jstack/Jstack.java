package com.untrackr.alerter.processor.filter.jstack;

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
			// First line after an exception line. Check if it's an "at" line.
			Matcher matcher = atLinePattern.matcher(line);
			ParsedException exception = state.getException();
			if (!matcher.matches()) {
				// We don't recognize an "at" line. Give up on finding the method and location.
				state.reset();
				return exception;
			}
			String method = matcher.group("method");
			String location = matcher.group("location");
			exception.setMethod(method);
			exception.setLocation(location);
			if ((methodPattern == null) || (methodPattern.matcher(method).find())) {
				// Found a satisfactory line in the stack. Return the exception
				state.reset();
				return exception;
			}
			// Keep looking for a satisfactory line
		}
		// Look for an exception line.
		Matcher matcher = exceptionLinePattern.matcher(line);
		if (matcher.matches()) {
			String name = matcher.group("name");
			String message = matcher.group("message");
			ParsedException exception = new ParsedException(name, message);
			state.setException(exception);
		}
		return null;
	}

	static class ParsingState {

		/**
		 * Set when the exception line was recognized
		 */
		private ParsedException exception;

		public void reset() {
			this.exception = null;
		}

		public ParsedException getException() {
			return exception;
		}

		public void setException(ParsedException exception) {
			this.exception = exception;
		}

	}

	public static class ParsedException {

		private String name;
		private String message;
		private String method;
		private String location;

		public ParsedException(String name, String message) {
			this.name = name;
			this.message = message;
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

	}

}

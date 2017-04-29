package org.opwatch.processor.config;

import org.opwatch.processor.common.FactoryExecutionScope;
import org.opwatch.processor.common.ProcessorFactory;
import org.opwatch.processor.common.RuntimeError;

import java.time.format.DateTimeParseException;

public class Duration {

	public enum ValueType {
		milliseconds, text
	}

	private ValueType type;
	private long milliseconds;
	private String text;

	private Duration() {
	}

	public static Duration makeMilliseconds(long milliseconds) {
		Duration duration = new Duration();
		duration.type = ValueType.milliseconds;
		duration.milliseconds = milliseconds;
		return duration;
	}

	public static Duration makeText(String text) {
		Duration duration = new Duration();
		duration.type = ValueType.text;
		duration.text = text;
		return duration;
	}

	public long value(ProcessorFactory<?, ?> factory) {
		if (type == ValueType.milliseconds) {
			return milliseconds;
		}
		String duration;
		int start;
		if (text.startsWith("P") || text.startsWith("p")) {
			duration = text;
			start = 0;
		} else {
			duration = "pt" + text;
			start = 2;
		}
		try {
			return java.time.Duration.parse(duration).toMillis();
		} catch (DateTimeParseException e) {
			throw new RuntimeError(e.getLocalizedMessage() + " at index " + (e.getErrorIndex() - start) + ": \"" + text + "\"",
					new FactoryExecutionScope(factory), e);
		}
	}

	public long getMilliseconds() {
		return milliseconds;
	}

	public String getText() {
		return text;
	}

}

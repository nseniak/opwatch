package org.opwatch.processor.common;

public class ApplicationInterruptedException extends RuntimeException {

	public static final String INTERRUPTION = "interruption";
	public static final String STREAM_CLOSED = "stream closed";

	public ApplicationInterruptedException(String message) {
		super(message);
	}

}

package com.untrackr.alerter.processor.producer.console;

import com.untrackr.alerter.processor.common.Payload;
import com.untrackr.alerter.processor.common.ProcessorLocation;

public class StdinPayload extends Payload {

	private int line;

	public StdinPayload(long timestamp, String hostname, ProcessorLocation producer, Payload previous, Object value, int line) {
		super(timestamp, hostname, producer, previous, value);
		this.line = line;
	}

	public int getLine() {
		return line;
	}

}

package com.untrackr.alerter.processor.primitives.producer.console;

import com.untrackr.alerter.processor.common.ProcessorLocation;
import com.untrackr.alerter.processor.payload.Payload;

public class StdinPayload extends Payload<String> {

	private int line;

	public StdinPayload(long timestamp, String hostname, ProcessorLocation producer, Payload previous, String value, int line) {
		super(timestamp, hostname, producer, previous, value);
		this.line = line;
	}

	public int getLine() {
		return line;
	}

}

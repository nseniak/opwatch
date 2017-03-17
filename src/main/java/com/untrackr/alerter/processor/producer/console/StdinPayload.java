package com.untrackr.alerter.processor.producer.console;

import com.untrackr.alerter.processor.common.Payload;
import com.untrackr.alerter.processor.common.ProcessorLocation;

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

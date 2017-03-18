package com.untrackr.alerter.processor.primitives.producer.tail;

import com.untrackr.alerter.processor.common.ProcessorLocation;
import com.untrackr.alerter.processor.payload.Payload;

public class TailPayload extends Payload<String> {

	private String file;
	private int line;

	public TailPayload(long timestamp, String hostname, ProcessorLocation producer, Payload previous, String value, String file, int line) {
		super(timestamp, hostname, producer, previous, value);
		this.file = file;
		this.line = line;
	}

	public String getFile() {
		return file;
	}

	public int getLine() {
		return line;
	}

}

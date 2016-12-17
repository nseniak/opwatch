package com.untrackr.alerter.processor.producer.tail;

import com.untrackr.alerter.processor.common.Payload;
import com.untrackr.alerter.processor.common.ProcessorLocation;

public class TailPayload extends Payload {

	private String file;
	private int line;

	public TailPayload(long timestamp, String hostname, ProcessorLocation producer, Payload previous, Object value, String file, int line) {
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

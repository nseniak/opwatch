package org.opwatch.processor.primitives.producer.console;

import org.opwatch.processor.payload.PayloadPojoValue;

public class StdinPayloadMetadata extends PayloadPojoValue {

	private int line;

	private StdinPayloadMetadata() {
	}

	public StdinPayloadMetadata(int line) {
		this.line = line;
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

}

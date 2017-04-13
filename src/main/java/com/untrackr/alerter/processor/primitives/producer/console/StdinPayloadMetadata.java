package com.untrackr.alerter.processor.primitives.producer.console;

public class StdinPayloadMetadata {

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

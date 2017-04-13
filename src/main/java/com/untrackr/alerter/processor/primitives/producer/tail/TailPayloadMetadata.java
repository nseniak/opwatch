package com.untrackr.alerter.processor.primitives.producer.tail;

public class TailPayloadMetadata {

	private String file;
	private int line;

	private TailPayloadMetadata() {
	}

	public TailPayloadMetadata(String file, int line) {
		this.file = file;
		this.line = line;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

}

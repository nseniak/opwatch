package com.untrackr.alerter.ioservice;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class LineReader {

	private BufferedInputStream inputStream;
	private int maxLineSize;
	private boolean afterCarriageReturn = false;
	private ByteArrayOutputStream currentLine = new ByteArrayOutputStream();

	public LineReader(BufferedInputStream inputStream, int maxLineSize) {
		this.inputStream = inputStream;
		this.maxLineSize = maxLineSize;
	}

	String readLine() throws IOException {
		currentLine.reset();
		inputStream.mark(maxLineSize);
		while (true) {
			int c = inputStream.read();
			if (c == -1) {
				try {
					inputStream.reset();
				} catch (IOException e) {
					afterCarriageReturn = false;
					return currentLine.toString();
				}
				return null;
			} else if ((c == '\n') && afterCarriageReturn) {
				// Ignore
				afterCarriageReturn = false;
			} else if ((c == '\n') || (c == '\r')) {
					afterCarriageReturn = (c == '\r');
					return currentLine.toString();
			} else {
				currentLine.write(c);
			}
		}
	}

	public void close() throws IOException {
		inputStream.close();
	}

}

/*
 * Copyright (c) 2016-2017 by OMC Inc and other Opwatch contributors
 *
 * Licensed under the Apache License, Version 2.0  (the "License").  You may obtain
 * a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied.  See the License for
 * the specific language governing permissions and limitations under the License.
 */

package org.opwatch.ioservice;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.ClosedByInterruptException;

/**
 * A line reader that always reads complete lines (with an end-of-line) and is interruptible -- unlike
 * BufferedReader.readLine().
 */
public class LineReader implements AutoCloseable {

	private BufferedInputStream inputStream;
	private int maxLineSize;
	private boolean afterCarriageReturn = false;
	private boolean forceInterruptible;
	private ByteArrayOutputStream currentLine = new ByteArrayOutputStream();

	public LineReader(BufferedInputStream inputStream, int maxLineSize, boolean forceInterruptible) {
		this.inputStream = inputStream;
		this.maxLineSize = maxLineSize;
		this.forceInterruptible = forceInterruptible;
	}

	public String readLine() throws IOException, InterruptedException {
		currentLine.reset();
		inputStream.mark(maxLineSize);
		try {
			while (true) {
				if (forceInterruptible) {
					if (inputStream.available() == 0) {
						Thread.sleep(100);
					}
				}
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
		} catch (ClosedByInterruptException e) {
			// Thrown by read() when the thread is interrupted.
			throw new InterruptedException(e.getMessage());
		}
	}

	public void close() throws IOException {
		inputStream.close();
	}

}

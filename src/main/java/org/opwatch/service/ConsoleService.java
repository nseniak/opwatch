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

package org.opwatch.service;

import org.opwatch.processor.common.ApplicationInterruptedException;
import org.opwatch.processor.common.RuntimeError;
import org.opwatch.processor.payload.PayloadPojoValue;
import org.opwatch.ioservice.LineReader;
import org.opwatch.processor.common.ActiveProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

@Service
public class ConsoleService {

	@Autowired
	ProcessorService processorService;

	private List<ConsoleLineConsumer> consumers = new ArrayList<>();

	private Future<?> consoleFuture;

	public void addConsumer(ConsoleLineConsumer consumer) {
		synchronized (consumers) {
			if (consumers.isEmpty()) {
				startConsoleThread();
			}
			consumers.add(consumer);
		}
	}

	public void removeConsumer(ActiveProcessor consumer) {
		synchronized (consumers) {
			consumers.remove(consumer);
			if (consumers.isEmpty()) {
				stopConsoleThread();
			}
		}
	}

	private void startConsoleThread() {
		consoleFuture = processorService.getConsumerExecutor().submit(() -> {
			int bufsize = processorService.config().lineBufferSize();
			LineReader reader = new LineReader(new BufferedInputStream(System.in), bufsize, true);
			// Since System.in.read() is not interruptible, we force its interruptibility using polling
			String line;
			int lineNumber = 0;
			try {
				while ((line = reader.readLine()) != null) {
					lineNumber = lineNumber + 1;
					ConsoleLine consoleLine = new ConsoleLine(line, lineNumber);
					for (ConsoleLineConsumer consumer : consumers) {
						consumer.consume(consoleLine);
					}
				}
			} catch (IOException e) {
				throw new RuntimeError("error reading from console: " + e.getMessage(), e);
			} catch (InterruptedException e) {
				// Shutting down.
				throw new ApplicationInterruptedException(ApplicationInterruptedException.INTERRUPTION);
			}
		});
	}

	private void stopConsoleThread() {
		consoleFuture.cancel(true);
	}

	public static class ConsoleLine extends PayloadPojoValue {

		private String text;
		private int line;

		public ConsoleLine(String text, int line) {
			this.text = text;
			this.line = line;
		}

		public String getText() {
			return text;
		}

		public int getLine() {
			return line;
		}

	}

	public interface ConsoleLineConsumer {

		public void consume(ConsoleLine line);

	}

}

package com.untrackr.alerter.service;

import com.untrackr.alerter.processor.payload.PayloadObjectValue;
import com.untrackr.alerter.ioservice.LineReader;
import com.untrackr.alerter.processor.common.ActiveProcessor;
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
			int bufsize = processorService.getProfileService().profile().getLineBufferSize();
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
				throw new RuntimeException(e);
			} catch (InterruptedException e) {
				// Exiting.. Nothing to do.
			}
		});
	}

	private void stopConsoleThread() {
		consoleFuture.cancel(true);
	}

	public static class ConsoleLine extends PayloadObjectValue {

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

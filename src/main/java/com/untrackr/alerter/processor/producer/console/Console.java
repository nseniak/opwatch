package com.untrackr.alerter.processor.producer.console;

import com.untrackr.alerter.common.ScriptObject;
import com.untrackr.alerter.ioservice.LineReader;
import com.untrackr.alerter.processor.common.IncludePath;
import com.untrackr.alerter.processor.producer.ThreadedProducer;
import com.untrackr.alerter.service.ProcessorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;

public class Console extends ThreadedProducer {

	private static final Logger logger = LoggerFactory.getLogger(Console.class);

	public Console(ProcessorService processorService, IncludePath path) {
		super(processorService, path);
	}

	@Override
	public void run() {
		int bufsize = processorService.getProfileService().profile().getLineBufferSize();
		LineReader reader = new LineReader(new BufferedInputStream(System.in), bufsize);
		// Since System.in.read() is not interruptible, we force its interruptibility using polling
		reader.setForceInterruptible(true);
		String line;
		int lineNumber = 0;
		try {
			while ((line = reader.readLine()) != null) {
				lineNumber = lineNumber + 1;
				LineObject lineObject = new LineObject();
				lineObject.text = line;
				lineObject.line = lineNumber;
				outputProduced(lineObject);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (InterruptedException e) {
			// Exiting.. Nothing to do.
		}
	}

	public static class LineObject extends ScriptObject {

		private String text;
		private int line;

		public String getText() {
			return text;
		}

		public int getLine() {
			return line;
		}

	}

}

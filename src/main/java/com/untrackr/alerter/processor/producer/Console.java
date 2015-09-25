package com.untrackr.alerter.processor.producer;

import com.untrackr.alerter.common.ThreadUtil;
import com.untrackr.alerter.model.descriptor.IncludePath;
import com.untrackr.alerter.service.ProcessorService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Console extends Producer {

	public Console(ProcessorService processorService, IncludePath path) {
		super(processorService, path);
	}

	@Override
	public void initialize() {
		ThreadUtil.threadFactory("Console", true).newThread(() -> {
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			String line;
			int lineNumber = 0;
			try {
				while ((line = reader.readLine()) != null) {
					lineNumber = lineNumber + 1;
					LineObject lineObject = new LineObject();
					lineObject.hostname = processorService.getHostName();
					lineObject.text = line;
					lineObject.line = lineNumber;
					output(lineObject, null);
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}).start();
	}

	public static class LineObject {

		public String hostname;
		public String text;
		public int line;

	}

}

package com.untrackr.alerter.processor.producer.console;

import com.untrackr.alerter.common.ScriptObject;
import com.untrackr.alerter.common.ThreadUtil;
import com.untrackr.alerter.processor.common.IncludePath;
import com.untrackr.alerter.processor.producer.Producer;
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
		ThreadUtil.threadFactory("Console").newThread(() -> {
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
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
			}
		}).start();
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

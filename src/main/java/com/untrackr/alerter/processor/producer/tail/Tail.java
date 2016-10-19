package com.untrackr.alerter.processor.producer.tail;

import com.untrackr.alerter.ioservice.TailedFile;
import com.untrackr.alerter.model.common.AlerterProfile;
import com.untrackr.alerter.processor.producer.Producer;
import com.untrackr.alerter.service.ProcessorService;

import java.nio.file.Path;

public class Tail extends Producer {

	private Path file;
	private boolean ignoreBlankLine;
	private TailedFile tailedFile;

	public Tail(ProcessorService processorService, String name, Path file, boolean ignoreBlankLine) {
		super(processorService, name);
		this.file = file;
		this.ignoreBlankLine = ignoreBlankLine;
	}

	@Override
	public void doStart() {
		AlerterProfile profile = getProcessorService().getProfileService().profile();
		tailedFile = new TailedFile(profile, file, (line, lineNumber) -> {
			if (ignoreBlankLine && line.trim().isEmpty()) {
				return;
			}
			LineObject lineObject = new LineObject();
			lineObject.file = file.toAbsolutePath().toString();
			lineObject.text = line;
			lineObject.line = lineNumber;
			outputProduced(lineObject);
		});
		getProcessorService().getFileTailingService().addTailedFile(tailedFile);
	}

	@Override
	public void doStop() {
		getProcessorService().getFileTailingService().removeTailedFile(tailedFile);
	}

	public static class LineObject {

		private String file;
		private String text;
		private int line;

		public String getFile() {
			return file;
		}

		public String getText() {
			return text;
		}

		public int getLine() {
			return line;
		}

	}

}

package com.untrackr.alerter.processor.producer.tail;

import com.untrackr.alerter.ioservice.TailedFile;
import com.untrackr.alerter.model.common.AlerterProfile;
import com.untrackr.alerter.processor.common.IncludePath;
import com.untrackr.alerter.processor.producer.Producer;
import com.untrackr.alerter.service.ProcessorService;

import java.nio.file.Path;

public class Tail extends Producer {

	private Path file;

	public Tail(ProcessorService processorService, IncludePath path, Path file) {
		super(processorService, path);
		this.file = file;
	}

	@Override
	public void initialize() {
		AlerterProfile profile = getProcessorService().getProfileService().profile();
		TailedFile tailedFile = new TailedFile(profile, file, (line, lineNumber) -> {
			LineObject lineObject = new LineObject();
			lineObject.file = file.toAbsolutePath().toString();
			lineObject.text = line;
			lineObject.line = lineNumber;
			outputProduced(lineObject);
		});
		getProcessorService().getFileTailingService().addTailedFile(tailedFile);
	}

	@Override
	public String identifier() {
		return file.toString();
	}

	public static class LineObject {

		public String file;
		public String text;
		public int line;

	}

}

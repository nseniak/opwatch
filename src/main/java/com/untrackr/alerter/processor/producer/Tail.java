package com.untrackr.alerter.processor.producer;

import com.untrackr.alerter.ioservice.TailedFile;
import com.untrackr.alerter.model.common.AlerterProfile;
import com.untrackr.alerter.model.descriptor.IncludePath;
import com.untrackr.alerter.service.ProcessorService;

import java.io.File;

public class Tail extends Producer {

	private File file;

	public Tail(ProcessorService processorService, IncludePath path, File file) {
		super(processorService, path);
		this.file = file;
	}

	@Override
	public void initialize() {
		AlerterProfile profile = getProcessorService().getProfileService().profile();
		TailedFile tailedFile = new TailedFile(profile, file, (line, lineNumber) -> {
			LineObject lineObject = new LineObject();
			lineObject.hostname = processorService.getHostName();
			lineObject.file = file.getAbsolutePath();
			lineObject.text = line;
			lineObject.line = lineNumber;
			output(lineObject, null);
		});
		getProcessorService().getFileTailingService().addTailedFile(tailedFile);
	}

	public static class LineObject {

		public String hostname;
		public String file;
		public String text;
		public int line;

	}

}

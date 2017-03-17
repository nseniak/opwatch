package com.untrackr.alerter.processor.producer.tail;

import com.untrackr.alerter.ioservice.TailedFile;
import com.untrackr.alerter.model.common.AlerterProfile;
import com.untrackr.alerter.processor.common.Payload;
import com.untrackr.alerter.processor.producer.Producer;
import com.untrackr.alerter.service.ProcessorService;

import java.nio.file.Path;

public class Tail extends Producer<TailDesc> {

	private Path file;
	private boolean ignoreBlankLine;
	private TailedFile tailedFile;

	public Tail(ProcessorService processorService, TailDesc descriptor, String name, Path file, boolean ignoreBlankLine) {
		super(processorService, descriptor, name);
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
			Payload payload = new TailPayload(System.currentTimeMillis(), processorService.getHostName(), location, null,
					line, file.toAbsolutePath().toString(), lineNumber);
			output(payload);
		});
		getProcessorService().getFileTailingService().addTailedFile(tailedFile);
	}

	@Override
	public void doStop() {
		getProcessorService().getFileTailingService().removeTailedFile(tailedFile);
	}

}

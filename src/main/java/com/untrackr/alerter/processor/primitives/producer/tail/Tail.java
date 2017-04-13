package com.untrackr.alerter.processor.primitives.producer.tail;

import com.untrackr.alerter.ioservice.TailedFile;
import com.untrackr.alerter.service.AlerterConfig;
import com.untrackr.alerter.processor.payload.Payload;
import com.untrackr.alerter.processor.primitives.producer.Producer;
import com.untrackr.alerter.service.ProcessorService;

import java.nio.file.Path;

public class Tail extends Producer<TailConfig> {

	private Path file;
	private boolean ignoreBlankLine;
	private TailedFile tailedFile;

	public Tail(ProcessorService processorService, TailConfig descriptor, String name, Path file, boolean ignoreBlankLine) {
		super(processorService, descriptor, name);
		this.file = file;
		this.ignoreBlankLine = ignoreBlankLine;
	}

	@Override
	public void start() {
		AlerterConfig profile = getProcessorService().config();
		tailedFile = new TailedFile(profile, file, (line, lineNumber) -> {
			if (ignoreBlankLine && line.trim().isEmpty()) {
				return;
			}
			Payload<String> payload = Payload.makeRoot(processorService, this, line);
			payload.setMetadata(new TailPayloadMetadata(file.toAbsolutePath().toString(), lineNumber));
			output(payload);
		});
		getProcessorService().getFileTailingService().addTailedFile(tailedFile);
	}

	@Override
	public void stop() {
		getProcessorService().getFileTailingService().removeTailedFile(tailedFile);
	}

}

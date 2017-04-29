package org.opwatch.processor.primitives.producer.tail;

import org.opwatch.ioservice.TailedFile;
import org.opwatch.service.Config;
import org.opwatch.processor.payload.Payload;
import org.opwatch.processor.primitives.producer.Producer;
import org.opwatch.service.ProcessorService;

import java.nio.file.Path;

public class Tail extends Producer<TailConfig> {

	private Path file;
	private boolean ignoreBlankLine;
	private TailedFile tailedFile;

	public Tail(ProcessorService processorService, TailConfig configuration, String name, Path file, boolean ignoreBlankLine) {
		super(processorService, configuration, name);
		this.file = file;
		this.ignoreBlankLine = ignoreBlankLine;
	}

	@Override
	public void start() {
		Config profile = getProcessorService().config();
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

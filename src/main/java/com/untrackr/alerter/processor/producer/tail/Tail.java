package com.untrackr.alerter.processor.producer.tail;

import com.untrackr.alerter.ioservice.TailedFile;
import com.untrackr.alerter.model.common.AlerterProfile;
import com.untrackr.alerter.processor.common.AlerterException;
import com.untrackr.alerter.processor.common.ExceptionContext;
import com.untrackr.alerter.processor.common.Payload;
import com.untrackr.alerter.processor.producer.Producer;
import com.untrackr.alerter.service.ProcessorService;

import java.nio.file.Path;

public class Tail extends Producer<TailDesc> {

	private Path file;
	private boolean json;
	private boolean ignoreBlankLine;
	private TailedFile tailedFile;

	public Tail(ProcessorService processorService, TailDesc descriptor, String name, Path file, boolean json, boolean ignoreBlankLine) {
		super(processorService, descriptor, name);
		this.file = file;
		this.json = json;
		this.ignoreBlankLine = ignoreBlankLine;
	}

	@Override
	public void doStart() {
		AlerterProfile profile = getProcessorService().getProfileService().profile();
		tailedFile = new TailedFile(profile, file, (line, lineNumber) -> {
			if (ignoreBlankLine && line.trim().isEmpty()) {
				return;
			}
			Object value = line;
			if (json) {
				try {
					processorService.parseJson(line);
				} catch (Throwable t) {
					// This code is running in a file tailing thread; throwing an exception would do no good as the exception
					// would be caught by this thread. We display the error.
					processorService.displayAlerterException(new AlerterException("cannot parse json at " + file + ":" + lineNumber + ": " + t.getMessage(),
							ExceptionContext.makeProcessorNoPayload(this)));
					return;
				}
			}
			Payload payload = new TailPayload(System.currentTimeMillis(), processorService.getHostName(), location, null,
					value, file.toAbsolutePath().toString(), lineNumber);
			output(payload);
		});
		getProcessorService().getFileTailingService().addTailedFile(tailedFile);
	}

	@Override
	public void doStop() {
		getProcessorService().getFileTailingService().removeTailedFile(tailedFile);
	}

}

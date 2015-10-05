package com.untrackr.alerter.processor.producer.console;

import com.untrackr.alerter.processor.common.IncludePath;
import com.untrackr.alerter.processor.producer.Producer;
import com.untrackr.alerter.service.ConsoleService;
import com.untrackr.alerter.service.ProcessorService;

public class Console extends Producer {

	private ConsoleService.ConsoleConsumer consoleConsumer;

	public Console(ProcessorService processorService, IncludePath path) {
		super(processorService, path);
	}

	@Override
	public void doStart() {
		consoleConsumer = this::outputProduced;
		processorService.getConsoleService().addConsumer(consoleConsumer);
	}

	@Override
	protected void doStop() {
		processorService.getConsoleService().removeConsumer(consoleConsumer);
	}

}

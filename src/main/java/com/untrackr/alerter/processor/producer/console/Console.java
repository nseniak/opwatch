package com.untrackr.alerter.processor.producer.console;

import com.untrackr.alerter.processor.common.IncludePath;
import com.untrackr.alerter.processor.producer.Producer;
import com.untrackr.alerter.service.ConsoleService;
import com.untrackr.alerter.service.ProcessorService;

public class Console extends Producer implements ConsoleService.ConsoleLineConsumer {

	public Console(ProcessorService processorService, IncludePath path) {
		super(processorService, path);
	}

	@Override
	public void doStart() {
		processorService.getConsoleService().addConsumer(this);
	}

	@Override
	protected void doStop() {
		processorService.getConsoleService().removeConsumer(this);
	}

	@Override
	public void consume(ConsoleService.ConsoleLine line) {
		outputProduced(line);
	}

}

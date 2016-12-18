package com.untrackr.alerter.processor.producer.console;

import com.untrackr.alerter.processor.producer.Producer;
import com.untrackr.alerter.service.ConsoleService;
import com.untrackr.alerter.service.ProcessorService;

public class Stdin extends Producer implements ConsoleService.ConsoleLineConsumer {

	public Stdin(ProcessorService processorService, StdinDesc descriptor, String name) {
		super(processorService, descriptor, name);
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
package com.untrackr.alerter.processor.producer.console;

import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.service.ProcessorService;

public class ConsoleFactory extends ActiveProcessorFactory {

	public ConsoleFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "console";
	}

	@Override
	public Console make(Object scriptObject) {
		ConsoleDesc descriptor = convertProcessorArgument(ConsoleDesc.class, scriptObject);
		Console console = new Console(getProcessorService(), displayName(descriptor));
		return console;
	}

}

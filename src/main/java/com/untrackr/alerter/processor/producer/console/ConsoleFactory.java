package com.untrackr.alerter.processor.producer.console;

import com.untrackr.alerter.model.common.JsonDescriptor;
import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.processor.common.IncludePath;
import com.untrackr.alerter.processor.common.ValidationError;
import com.untrackr.alerter.service.ProcessorService;

public class ConsoleFactory extends ActiveProcessorFactory {

	public ConsoleFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String type() {
		return "console";
	}

	@Override
	public Console make(JsonDescriptor jsonDescriptor, IncludePath path) throws ValidationError {
		ConsoleDesc descriptor = convertDescriptor(path, ConsoleDesc.class, jsonDescriptor);
		Console console = new Console(getProcessorService(), path);
		initialize(console, descriptor);
		return console;
	}

}

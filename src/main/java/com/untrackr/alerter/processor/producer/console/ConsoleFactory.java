package com.untrackr.alerter.processor.producer.console;

import com.untrackr.alerter.model.common.JsonDescriptor;
import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.common.ScriptStack;
import com.untrackr.alerter.processor.common.ValidationError;
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
	public Processor make(Object object) throws ValidationError {
		JsonDescriptor jsonDescriptor = scriptDescriptor(object);
		ConsoleDesc descriptor = convertScriptDescriptor(ConsoleDesc.class, jsonDescriptor);
		Console console = new Console(getProcessorService(), ScriptStack.currentStack());
		initialize(console, descriptor);
		return console;
	}

}

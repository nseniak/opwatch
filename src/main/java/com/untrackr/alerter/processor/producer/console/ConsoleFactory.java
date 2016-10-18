package com.untrackr.alerter.processor.producer.console;

import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.common.ScriptStack;
import com.untrackr.alerter.processor.common.RuntimeScriptException;
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
	public Processor make(Object scriptObject) throws RuntimeScriptException {
		ConsoleDesc descriptor = convertProcessorArgument(ConsoleDesc.class, scriptObject);
		Console console = new Console(getProcessorService(), ScriptStack.currentStack());
		initialize(console, descriptor);
		return console;
	}

}

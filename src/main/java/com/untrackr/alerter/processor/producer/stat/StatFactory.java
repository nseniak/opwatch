package com.untrackr.alerter.processor.producer.stat;

import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.common.ScriptStack;
import com.untrackr.alerter.processor.common.RuntimeScriptException;
import com.untrackr.alerter.processor.producer.ScheduledExecutorFactory;
import com.untrackr.alerter.service.ProcessorService;

public class StatFactory extends ScheduledExecutorFactory {

	public StatFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "stat";
	}

	@Override
	public Processor make(Object scriptObject) throws RuntimeScriptException {
		StatDesc descriptor = convertProcessorArgument(StatDesc.class, scriptObject);
		String file = checkVariableSubstitution("file", checkFieldValue("file", descriptor.getFile()));
		Stat stat = new Stat(getProcessorService(), ScriptStack.currentStack(), makeScheduledExecutor(descriptor), new java.io.File(file));
		initialize(stat, descriptor);
		return stat;
	}

}

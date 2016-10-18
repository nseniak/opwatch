package com.untrackr.alerter.processor.producer.count;

import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.common.ScriptStack;
import com.untrackr.alerter.processor.common.RuntimeScriptException;
import com.untrackr.alerter.processor.producer.ScheduledExecutorFactory;
import com.untrackr.alerter.service.ProcessorService;

public class CountFactory extends ScheduledExecutorFactory {

	public CountFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "count";
	}

	@Override
	public Processor make(Object scriptObject) throws RuntimeScriptException {
		CountDesc descriptor = convertProcessorArgument(CountDesc.class, scriptObject);
		Count count = new Count(getProcessorService(), ScriptStack.currentStack(), makeScheduledExecutor(descriptor));
		initialize(count, descriptor);
		return count;
	}

}

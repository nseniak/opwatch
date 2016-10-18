package com.untrackr.alerter.processor.transformer.print;

import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.common.ScriptStack;
import com.untrackr.alerter.processor.common.RuntimeScriptException;
import com.untrackr.alerter.service.ProcessorService;

public class EchoFactory extends ActiveProcessorFactory {

	public EchoFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "echo";
	}

	@Override
	public Processor make(Object scriptObject) throws RuntimeScriptException {
		EchoDesc descriptor = convertProcessorArgument(EchoDesc.class, scriptObject);
		Echo echo = new Echo(getProcessorService(), ScriptStack.currentStack());
		initialize(echo, descriptor);
		return echo;
	}

}

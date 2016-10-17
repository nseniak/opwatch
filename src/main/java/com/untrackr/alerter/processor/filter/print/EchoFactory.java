package com.untrackr.alerter.processor.filter.print;

import com.untrackr.alerter.model.common.JsonDescriptor;
import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.common.ScriptStack;
import com.untrackr.alerter.processor.common.ValidationError;
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
	public Processor make(Object object) throws ValidationError {
		JsonDescriptor jsonDescriptor = scriptDescriptor(object);
		EchoDesc descriptor = convertScriptDescriptor(EchoDesc.class, jsonDescriptor);
		Echo echo = new Echo(getProcessorService(), ScriptStack.currentStack());
		initialize(echo, descriptor);
		return echo;
	}

}

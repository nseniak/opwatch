package com.untrackr.alerter.processor.producer.console;

import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.service.ProcessorService;

public class StdinFactory extends ActiveProcessorFactory {

	public StdinFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "stdin";
	}

	@Override
	public Stdin make(Object scriptObject) {
		StdinDesc descriptor = convertProcessorArgument(StdinDesc.class, scriptObject);
		Stdin stdin = new Stdin(getProcessorService(), displayName(descriptor));
		return stdin;
	}

}

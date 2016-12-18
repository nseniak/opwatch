package com.untrackr.alerter.processor.producer.console;

import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.service.ProcessorService;

public class StdinFactory extends ActiveProcessorFactory {

	public StdinFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String type() {
		return "stdin";
	}

	@Override
	public Stdin make(Object scriptObject) {
		StdinDesc descriptor = convertProcessorDescriptor(StdinDesc.class, scriptObject);
		Stdin stdin = new Stdin(getProcessorService(), descriptor, type());
		return stdin;
	}

}

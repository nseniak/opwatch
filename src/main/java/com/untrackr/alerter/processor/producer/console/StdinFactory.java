package com.untrackr.alerter.processor.producer.console;

import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.service.ProcessorService;

public class StdinFactory extends ActiveProcessorFactory<StdinDesc, Stdin> {

	public StdinFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String type() {
		return "stdin";
	}

	@Override
	public Class<StdinDesc> descriptorClass() {
		return StdinDesc.class;
	}

	@Override
	public Stdin make(Object scriptObject) {
		StdinDesc descriptor = convertProcessorDescriptor(scriptObject);
		Stdin stdin = new Stdin(getProcessorService(), descriptor, type());
		return stdin;
	}

}

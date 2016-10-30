package com.untrackr.alerter.processor.transformer.print;

import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.service.ProcessorService;

public class StdoutFactory extends ActiveProcessorFactory {

	public StdoutFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "cat";
	}

	@Override
	public Stdout make(Object scriptObject) {
		StdoutDesc descriptor = convertProcessorArgument(StdoutDesc.class, scriptObject);
		Stdout stdout = new Stdout(getProcessorService(), displayName(descriptor));
		return stdout;
	}

}

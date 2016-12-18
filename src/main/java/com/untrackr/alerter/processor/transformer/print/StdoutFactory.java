package com.untrackr.alerter.processor.transformer.print;

import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.service.ProcessorService;

public class StdoutFactory extends ActiveProcessorFactory {

	public StdoutFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String type() {
		return "cat";
	}

	@Override
	public Stdout make(Object scriptObject) {
		StdoutDesc descriptor = convertProcessorDescriptor(StdoutDesc.class, scriptObject);
		Stdout stdout = new Stdout(getProcessorService(), descriptor, type());
		return stdout;
	}

}

package com.untrackr.alerter.processor.transformer.print;

import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.service.ProcessorService;

public class StdoutFactory extends ActiveProcessorFactory<StdoutDesc, Stdout> {

	public StdoutFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String type() {
		return "stdout";
	}

	@Override
	public Class<StdoutDesc> descriptorClass() {
		return StdoutDesc.class;
	}

	@Override
	public Stdout make(Object scriptObject) {
		StdoutDesc descriptor = convertProcessorDescriptor(scriptObject);
		boolean displayPayload = optionalPropertyValue("payload", descriptor.getPayload(), false);
		Stdout stdout = new Stdout(getProcessorService(), descriptor, type(), displayPayload);
		return stdout;
	}

}

package com.untrackr.alerter.processor.primitives.filter.stdout;

import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.service.ProcessorService;

public class StdoutFactory extends ActiveProcessorFactory<StdoutConfig, Stdout> {

	public StdoutFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "stdout";
	}

	@Override
	public Class<StdoutConfig> descriptorClass() {
		return StdoutConfig.class;
	}

	@Override
	public Stdout make(Object scriptObject) {
		StdoutConfig descriptor = convertProcessorDescriptor(scriptObject);
		boolean displayPayload = optionalPropertyValue("payload", descriptor.getPayload(), false);
		Stdout stdout = new Stdout(getProcessorService(), descriptor, name(), displayPayload);
		return stdout;
	}

}

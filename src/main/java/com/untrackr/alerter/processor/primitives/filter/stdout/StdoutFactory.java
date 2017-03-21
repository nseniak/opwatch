package com.untrackr.alerter.processor.primitives.filter.stdout;

import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.service.ProcessorService;

public class StdoutFactory extends ActiveProcessorFactory<StdoutDescriptor, Stdout> {

	public StdoutFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "stdout";
	}

	@Override
	public Class<StdoutDescriptor> descriptorClass() {
		return StdoutDescriptor.class;
	}

	@Override
	public Stdout make(Object scriptObject) {
		StdoutDescriptor descriptor = convertProcessorDescriptor(scriptObject);
		boolean displayPayload = optionalPropertyValue("payload", descriptor.getPayload(), false);
		Stdout stdout = new Stdout(getProcessorService(), descriptor, name(), displayPayload);
		return stdout;
	}

}

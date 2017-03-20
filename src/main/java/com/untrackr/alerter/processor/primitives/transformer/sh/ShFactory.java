package com.untrackr.alerter.processor.primitives.transformer.sh;

import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.processor.primitives.producer.CommandRunner;
import com.untrackr.alerter.service.ProcessorService;

public class ShFactory extends ActiveProcessorFactory<ShDescriptor, Sh> {

	public ShFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "sh";
	}

	@Override
	public Class<ShDescriptor> descriptorClass() {
		return ShDescriptor.class;
	}

	@Override
	public Sh make(Object scriptObject) {
		ShDescriptor descriptor = convertProcessorDescriptor(scriptObject);
		CommandRunner producer = makeCommandOutputProducer(descriptor);
		Sh sh = new Sh(getProcessorService(), descriptor, name(), producer);
		return sh;
	}

}

package com.untrackr.alerter.processor.transformer.sh;

import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.processor.producer.CommandRunner;
import com.untrackr.alerter.service.ProcessorService;

public class ShFactory extends ActiveProcessorFactory {

	public ShFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String type() {
		return "sh";
	}

	@Override
	public Class<ShDesc> descriptorClass() {
		return ShDesc.class;
	}

	@Override
	public Sh make(Object scriptObject) {
		ShDesc descriptor = convertProcessorDescriptor(ShDesc.class, scriptObject);
		CommandRunner producer = makeCommandOutputProducer(descriptor);
		Sh sh = new Sh(getProcessorService(), descriptor, type(), producer);
		return sh;
	}

}

package com.untrackr.alerter.processor.transformer.sh;

import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.producer.CommandRunner;
import com.untrackr.alerter.service.ProcessorService;

public class ShFactory extends ActiveProcessorFactory {

	public ShFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "sh";
	}

	@Override
	public Processor make(Object scriptObject) {
		ShDesc descriptor = convertProcessorArgument(ShDesc.class, scriptObject);
		CommandRunner producer = makeCommandOutputProducer(descriptor);
		Sh sh = new Sh(getProcessorService(), displayName(descriptor), producer);
		return sh;
	}

}

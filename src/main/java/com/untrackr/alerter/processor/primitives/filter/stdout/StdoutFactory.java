package com.untrackr.alerter.processor.primitives.filter.stdout;

import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.processor.common.ProcessorSignature;
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
	public Class<StdoutConfig> configurationClass() {
		return StdoutConfig.class;
	}

	@Override
	public Class<Stdout> processorClass() {
		return Stdout.class;
	}

	@Override
	public ProcessorSignature staticSignature() {
		return ProcessorSignature.makeSideEffectFilter();
	}

	@Override
	public Stdout make(Object scriptObject) {
		StdoutConfig descriptor = convertProcessorDescriptor(scriptObject);
		boolean displayPayload = descriptor.getPayload();
		return new Stdout(getProcessorService(), descriptor, name(), displayPayload);
	}

}

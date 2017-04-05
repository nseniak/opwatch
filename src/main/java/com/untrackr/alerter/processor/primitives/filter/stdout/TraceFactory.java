package com.untrackr.alerter.processor.primitives.filter.stdout;

import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.processor.common.ProcessorSignature;
import com.untrackr.alerter.service.ProcessorService;

public class TraceFactory extends ActiveProcessorFactory<TraceConfig, Trace> {

	public TraceFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "trace";
	}

	@Override
	public Class<TraceConfig> configurationClass() {
		return TraceConfig.class;
	}

	@Override
	public Class<Trace> processorClass() {
		return Trace.class;
	}

	@Override
	public ProcessorSignature staticSignature() {
		return ProcessorSignature.makeSideEffectFilter();
	}

	@Override
	public Trace make(Object scriptObject) {
		TraceConfig config = convertProcessorConfig(scriptObject);
		boolean displayPayload = checkPropertyValue("payload", config.getPayload());
		return new Trace(getProcessorService(), config, name(), displayPayload);
	}

}

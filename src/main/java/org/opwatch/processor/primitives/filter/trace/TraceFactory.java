package org.opwatch.processor.primitives.filter.trace;

import org.opwatch.processor.common.ActiveProcessorFactory;
import org.opwatch.processor.common.ProcessorSignature;
import org.opwatch.service.ProcessorService;

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
		return new Trace(getProcessorService(), config, name());
	}

}

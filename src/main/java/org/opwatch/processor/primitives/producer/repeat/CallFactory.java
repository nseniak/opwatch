package org.opwatch.processor.primitives.producer.repeat;

import org.opwatch.processor.common.ProcessorSignature;
import org.opwatch.processor.config.JavascriptProducer;
import org.opwatch.processor.primitives.producer.ScheduledExecutor;
import org.opwatch.processor.primitives.producer.ScheduledExecutorFactory;
import org.opwatch.service.ProcessorService;

public class CallFactory extends ScheduledExecutorFactory<CallConfig, Call> {

	public CallFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "call";
	}

	@Override
	public Class<CallConfig> configurationClass() {
		return CallConfig.class;
	}

	@Override
	public Class<Call> processorClass() {
		return Call.class;
	}

	@Override
	public ProcessorSignature staticSignature() {
		return ProcessorSignature.makeProducer();
	}

	@Override
	public Call make(Object scriptObject) {
		CallConfig config = convertProcessorConfig(scriptObject);
		ScheduledExecutor executor = makeScheduledExecutor(config);
		JavascriptProducer producer = checkPropertyValue("lambda", config.getLambda());
		return new Call(getProcessorService(), config, name(), executor, producer);
	}

}

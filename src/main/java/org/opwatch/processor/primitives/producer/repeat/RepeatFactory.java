package org.opwatch.processor.primitives.producer.repeat;

import org.opwatch.processor.common.ProcessorSignature;
import org.opwatch.processor.config.JavascriptProducer;
import org.opwatch.processor.primitives.producer.ScheduledExecutor;
import org.opwatch.processor.primitives.producer.ScheduledExecutorFactory;
import org.opwatch.service.ProcessorService;

public class RepeatFactory extends ScheduledExecutorFactory<RepeatConfig, Repeat> {

	public RepeatFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "repeat";
	}

	@Override
	public Class<RepeatConfig> configurationClass() {
		return RepeatConfig.class;
	}

	@Override
	public Class<Repeat> processorClass() {
		return Repeat.class;
	}

	@Override
	public ProcessorSignature staticSignature() {
		return ProcessorSignature.makeProducer();
	}

	@Override
	public Repeat make(Object scriptObject) {
		RepeatConfig config = convertProcessorConfig(scriptObject);
		ScheduledExecutor executor = makeScheduledExecutor(config);
		JavascriptProducer producer = checkPropertyValue("lambda", config.getLambda());
		return new Repeat(getProcessorService(), config, name(), executor, producer);
	}

}

package org.opwatch.processor.primitives.producer.receive;

import org.opwatch.processor.common.ActiveProcessorFactory;
import org.opwatch.processor.common.ProcessorSignature;
import org.opwatch.service.ProcessorService;

public class ReceiveFactory extends ActiveProcessorFactory<ReceiveConfig, Receive> {

	public ReceiveFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "receive";
	}

	@Override
	public Class<ReceiveConfig> configurationClass() {
		return ReceiveConfig.class;
	}

	@Override
	public Class<Receive> processorClass() {
		return Receive.class;
	}

	@Override
	public ProcessorSignature staticSignature() {
		return ProcessorSignature.makeProducer();
	}

	@Override
	public Receive make(Object scriptObject) {
		ReceiveConfig config = convertProcessorConfig(scriptObject);
		String urlPath = checkPropertyValue("url", config.getPath());
		return new Receive(getProcessorService(), config, name(), urlPath);
	}

}

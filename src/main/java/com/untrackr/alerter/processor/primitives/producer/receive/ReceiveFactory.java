package com.untrackr.alerter.processor.primitives.producer.receive;

import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.processor.common.ProcessorSignature;
import com.untrackr.alerter.service.ProcessorService;

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

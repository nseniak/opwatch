package com.untrackr.alerter.processor.primitives.producer.receive;

import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
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
	public Receive make(Object scriptObject) {
		ReceiveConfig descriptor = convertProcessorDescriptor(scriptObject);
		String urlPath = checkPropertyValue("url", descriptor.getPath());
		Receive receive = new Receive(getProcessorService(), descriptor, name(), urlPath);
		return receive;
	}

}

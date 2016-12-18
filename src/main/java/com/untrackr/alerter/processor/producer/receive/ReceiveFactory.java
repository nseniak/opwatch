package com.untrackr.alerter.processor.producer.receive;

import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.service.ProcessorService;

public class ReceiveFactory extends ActiveProcessorFactory {

	public ReceiveFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String type() {
		return "receive";
	}

	@Override
	public Receive make(Object scriptObject) {
		ReceiveDesc descriptor = convertProcessorDescriptor(ReceiveDesc.class, scriptObject);
		String urlPath = checkPropertyValue("url", descriptor.getPath());
		Receive receive = new Receive(getProcessorService(), descriptor, type(), urlPath);
		return receive;
	}

}

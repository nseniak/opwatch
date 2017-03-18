package com.untrackr.alerter.processor.primitives.producer.receive;

import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.service.ProcessorService;

public class ReceiveFactory extends ActiveProcessorFactory<ReceiveDescriptor, Receive> {

	public ReceiveFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String type() {
		return "receive";
	}

	@Override
	public Class<ReceiveDescriptor> descriptorClass() {
		return ReceiveDescriptor.class;
	}

	@Override
	public Receive make(Object scriptObject) {
		ReceiveDescriptor descriptor = convertProcessorDescriptor(scriptObject);
		String urlPath = checkPropertyValue("url", descriptor.getPath());
		Receive receive = new Receive(getProcessorService(), descriptor, type(), urlPath);
		return receive;
	}

}

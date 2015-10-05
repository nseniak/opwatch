package com.untrackr.alerter.processor.producer.receive;

import com.untrackr.alerter.model.common.JsonDescriptor;
import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.processor.common.IncludePath;
import com.untrackr.alerter.processor.common.ValidationError;
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
	public Receive make(JsonDescriptor jsonDescriptor, IncludePath path) throws ValidationError {
		ReceiveDesc descriptor = convertDescriptor(path, ReceiveDesc.class, jsonDescriptor);
		String urlPath = checkFieldValue(path, jsonDescriptor, "url", descriptor.getPath());
		Receive receive = new Receive(getProcessorService(), path, urlPath);
		initialize(receive, descriptor);
		return receive;
	}

}

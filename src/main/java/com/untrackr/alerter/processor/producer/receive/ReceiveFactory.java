package com.untrackr.alerter.processor.producer.receive;

import com.untrackr.alerter.model.common.JsonDescriptor;
import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.common.ScriptStack;
import com.untrackr.alerter.processor.common.ValidationError;
import com.untrackr.alerter.service.ProcessorService;

public class ReceiveFactory extends ActiveProcessorFactory {

	public ReceiveFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "receive";
	}

	@Override
	public Processor make(Object object) throws ValidationError {
		JsonDescriptor jsonDescriptor = scriptDescriptor(object);
		ReceiveDesc descriptor = convertScriptDescriptor(ReceiveDesc.class, jsonDescriptor);
		String urlPath = checkFieldValue(jsonDescriptor, "url", descriptor.getPath());
		Receive receive = new Receive(getProcessorService(), ScriptStack.currentStack(), urlPath);
		initialize(receive, descriptor);
		return receive;
	}

}

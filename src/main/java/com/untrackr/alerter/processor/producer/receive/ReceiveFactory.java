package com.untrackr.alerter.processor.producer.receive;

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
	public Processor make(Object scriptObject) throws ValidationError {
		ReceiveDesc descriptor = convertProcessorArgument(ReceiveDesc.class, scriptObject);
		String urlPath = checkFieldValue("url", descriptor.getPath());
		Receive receive = new Receive(getProcessorService(), ScriptStack.currentStack(), urlPath);
		initialize(receive, descriptor);
		return receive;
	}

}

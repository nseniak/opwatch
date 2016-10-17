package com.untrackr.alerter.processor.filter.sh;

import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.common.ScriptStack;
import com.untrackr.alerter.processor.common.ValidationError;
import com.untrackr.alerter.processor.producer.CommandRunner;
import com.untrackr.alerter.service.ProcessorService;

public class ShFactory extends ActiveProcessorFactory {

	public ShFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "sh";
	}

	@Override
	public Processor make(Object scriptObject) throws ValidationError {
		ShDesc descriptor = convertProcessorArgument(ShDesc.class, scriptObject);
		CommandRunner producer = makeCommandOutputProducer(descriptor);
		Sh sh = new Sh(getProcessorService(), ScriptStack.currentStack(), producer);
		initialize(sh, descriptor);
		return sh;
	}

}

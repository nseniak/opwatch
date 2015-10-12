package com.untrackr.alerter.processor.filter.sh;

import com.untrackr.alerter.model.common.JsonDescriptor;
import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.processor.common.IncludePath;
import com.untrackr.alerter.processor.common.ValidationError;
import com.untrackr.alerter.processor.producer.CommandRunner;
import com.untrackr.alerter.service.ProcessorService;

public class ShFactory extends ActiveProcessorFactory {

	public ShFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String type() {
		return "sh";
	}

	@Override
	public Sh make(JsonDescriptor jsonDescriptor, IncludePath path) throws ValidationError {
		ShDesc descriptor = convertDescriptor(path, ShDesc.class, jsonDescriptor);
		CommandRunner producer = makeCommandOutputProducer(path, jsonDescriptor, descriptor);
		Sh sh = new Sh(getProcessorService(), path, producer);
		initialize(sh, descriptor);
		return sh;
	}

}

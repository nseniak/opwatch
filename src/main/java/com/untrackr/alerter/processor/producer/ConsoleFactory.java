package com.untrackr.alerter.processor.producer;

import com.untrackr.alerter.model.common.JsonObject;
import com.untrackr.alerter.model.descriptor.IncludePath;
import com.untrackr.alerter.processor.common.Factory;
import com.untrackr.alerter.processor.common.ValidationError;
import com.untrackr.alerter.service.ProcessorService;

public class ConsoleFactory extends Factory {

	public ConsoleFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String type() {
		return "console";
	}

	@Override
	public Console make(JsonObject jsonObject, IncludePath path) throws ValidationError {
		return new Console(getProcessorService(), path);
	}

}

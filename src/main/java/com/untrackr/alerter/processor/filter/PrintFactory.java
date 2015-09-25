package com.untrackr.alerter.processor.filter;

import com.untrackr.alerter.model.common.JsonObject;
import com.untrackr.alerter.model.descriptor.IncludePath;
import com.untrackr.alerter.processor.common.Factory;
import com.untrackr.alerter.processor.common.ValidationError;
import com.untrackr.alerter.service.ProcessorService;

public class PrintFactory extends Factory {

	public PrintFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String type() {
		return "print";
	}

	@Override
	public Print make(JsonObject jsonObject, IncludePath path) throws ValidationError {
		return new Print(getProcessorService(), path);
	}

}

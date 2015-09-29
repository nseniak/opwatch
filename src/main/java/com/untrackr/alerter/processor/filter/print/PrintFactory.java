package com.untrackr.alerter.processor.filter.print;

import com.untrackr.alerter.model.common.JsonObject;
import com.untrackr.alerter.processor.common.IncludePath;
import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.processor.common.ValidationError;
import com.untrackr.alerter.service.ProcessorService;

public class PrintFactory extends ActiveProcessorFactory {

	public PrintFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String type() {
		return "print";
	}

	@Override
	public Print make(JsonObject jsonObject, IncludePath path) throws ValidationError {
		PrintDesc descriptor = convertDescriptor(path, PrintDesc.class, jsonObject);
		Print print = new Print(getProcessorService(), path);
		initialize(print, descriptor);
		return print;
	}

}

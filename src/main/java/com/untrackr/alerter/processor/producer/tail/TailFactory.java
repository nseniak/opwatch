package com.untrackr.alerter.processor.producer.tail;

import com.untrackr.alerter.model.common.JsonDescriptor;
import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.processor.common.IncludePath;
import com.untrackr.alerter.processor.common.ValidationError;
import com.untrackr.alerter.service.ProcessorService;

import java.io.File;

public class TailFactory extends ActiveProcessorFactory {

	public TailFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String type() {
		return "tail";
	}

	@Override
	public Tail make(JsonDescriptor jsonDescriptor, IncludePath path) throws ValidationError {
		TailDesc descriptor = convertDescriptor(path, TailDesc.class, jsonDescriptor);
		String file = checkVariableSubstitution(path, jsonDescriptor, "file", checkFieldValue(path, jsonDescriptor, "file", descriptor.getFile()));
		Tail tail = new Tail(getProcessorService(), path, new File(file));
		initialize(tail, descriptor);
		return tail;
	}

}

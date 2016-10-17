package com.untrackr.alerter.processor.producer.tail;

import com.untrackr.alerter.model.common.JsonDescriptor;
import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.common.ScriptStack;
import com.untrackr.alerter.processor.common.ValidationError;
import com.untrackr.alerter.service.ProcessorService;

import java.nio.file.FileSystems;

public class TailFactory extends ActiveProcessorFactory {

	public TailFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "tail";
	}

	@Override
	public Processor make(Object object) throws ValidationError {
		JsonDescriptor jsonDescriptor = scriptDescriptor(object);
		TailDesc descriptor = convertScriptDescriptor(TailDesc.class, jsonDescriptor);
		String file = checkVariableSubstitution(jsonDescriptor, "file", checkFieldValue(jsonDescriptor, "file", descriptor.getFile()));
		boolean ignoreBlankLine = optionalFieldValue(jsonDescriptor, "insecure", descriptor.isIgnoreBlankLine(), false);
		Tail tail = new Tail(getProcessorService(), ScriptStack.currentStack(), FileSystems.getDefault().getPath(file), ignoreBlankLine);
		initialize(tail, descriptor);
		return tail;
	}

}

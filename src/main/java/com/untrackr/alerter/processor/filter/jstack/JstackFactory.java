package com.untrackr.alerter.processor.filter.jstack;

import com.untrackr.alerter.model.common.JsonDescriptor;
import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.processor.common.IncludePath;
import com.untrackr.alerter.processor.common.ValidationError;
import com.untrackr.alerter.service.ProcessorService;

import java.util.regex.Pattern;

public class JstackFactory extends ActiveProcessorFactory {

	public JstackFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String type() {
		return "jstack";
	}

	@Override
	public Jstack make(JsonDescriptor jsonDescriptor, IncludePath path) throws ValidationError {
		JstackDesc descriptor = convertDescriptor(path, JstackDesc.class, jsonDescriptor);
		String fieldName = optionalFieldValue(path, jsonDescriptor, "field", descriptor.getField(), "text");
		String methodRegex = optionalFieldValue(path, jsonDescriptor, "methodRegex", descriptor.getMethodRegex(), null);
		Pattern methodPattern = (methodRegex == null) ? null : compilePattern(path, jsonDescriptor, "regex", methodRegex);
		Jstack jstack = new Jstack(getProcessorService(), path, fieldName, methodPattern);
		initialize(jstack, descriptor);
		return jstack;
	}

}

package com.untrackr.alerter.processor.primitives.transformer.jstack;

import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.service.ProcessorService;

import java.util.regex.Pattern;

public class JstackFactory extends ActiveProcessorFactory<JstackDescriptor, Jstack> {

	public JstackFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "jstack";
	}

	@Override
	public Class<JstackDescriptor> descriptorClass() {
		return JstackDescriptor.class;
	}

	@Override
	public Jstack make(Object scriptObject) {
		JstackDescriptor descriptor = convertProcessorDescriptor(scriptObject);
		String fieldName = optionalPropertyValue("field", descriptor.getField(), "text");
		String methodRegex = descriptor.getMethodRegex();
		Pattern methodPattern = (methodRegex == null) ? null : compilePattern("regex", methodRegex);
		Jstack jstack = new Jstack(getProcessorService(), descriptor, name(), fieldName, methodPattern);
		return jstack;
	}

}

package com.untrackr.alerter.processor.transformer.jstack;

import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.service.ProcessorService;

import java.util.regex.Pattern;

public class JstackFactory extends ActiveProcessorFactory<JstackDesc, Jstack> {

	public JstackFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String type() {
		return "jstack";
	}

	@Override
	public Class<JstackDesc> descriptorClass() {
		return JstackDesc.class;
	}

	@Override
	public Jstack make(Object scriptObject) {
		JstackDesc descriptor = convertProcessorDescriptor(scriptObject);
		String fieldName = optionalPropertyValue("field", descriptor.getField(), "text");
		String methodRegex = descriptor.getMethodRegex();
		Pattern methodPattern = (methodRegex == null) ? null : compilePattern("regex", methodRegex);
		Jstack jstack = new Jstack(getProcessorService(), descriptor, type(), fieldName, methodPattern);
		return jstack;
	}

}

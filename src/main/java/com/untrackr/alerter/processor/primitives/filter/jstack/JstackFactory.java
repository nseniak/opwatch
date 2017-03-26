package com.untrackr.alerter.processor.primitives.filter.jstack;

import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.service.ProcessorService;

import java.util.regex.Pattern;

public class JstackFactory extends ActiveProcessorFactory<JstackConfig, Jstack> {

	public JstackFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "jstack";
	}

	@Override
	public Class<JstackConfig> configurationClass() {
		return JstackConfig.class;
	}

	@Override
	public Jstack make(Object scriptObject) {
		JstackConfig descriptor = convertProcessorDescriptor(scriptObject);
		String methodRegex = descriptor.getMethodRegex();
		Pattern methodPattern = (methodRegex == null) ? null : compilePattern("regex", methodRegex);
		Jstack jstack = new Jstack(getProcessorService(), descriptor, name(), methodPattern);
		return jstack;
	}

}

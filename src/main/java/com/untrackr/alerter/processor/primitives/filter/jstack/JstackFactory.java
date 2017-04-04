package com.untrackr.alerter.processor.primitives.filter.jstack;

import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.processor.common.ProcessorSignature;
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
	public Class<Jstack> processorClass() {
		return Jstack.class;
	}

	@Override
	public ProcessorSignature staticSignature() {
		return ProcessorSignature.makeFilter();
	}

	@Override
	public Jstack make(Object scriptObject) {
		JstackConfig config = convertProcessorDescriptor(scriptObject);
		String methodRegex = config.getMethodRegex();
		Pattern methodPattern = (methodRegex == null) ? null : compilePattern("regex", methodRegex);
		return new Jstack(getProcessorService(), config, name(), methodPattern);
	}

}

package org.opwatch.processor.primitives.filter.jstack;

import org.opwatch.processor.common.ActiveProcessorFactory;
import org.opwatch.processor.common.ProcessorSignature;
import org.opwatch.service.ProcessorService;

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
		JstackConfig config = convertProcessorConfig(scriptObject);
		String methodRegex = config.getMethodRegex();
		Pattern methodPattern = (methodRegex == null) ? null : compilePattern("regex", methodRegex);
		return new Jstack(getProcessorService(), config, name(), methodPattern);
	}

}

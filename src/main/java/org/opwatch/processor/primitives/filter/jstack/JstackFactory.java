package org.opwatch.processor.primitives.filter.jstack;

import jdk.nashorn.internal.objects.NativeRegExp;
import org.opwatch.processor.common.ActiveProcessorFactory;
import org.opwatch.processor.common.ProcessorSignature;
import org.opwatch.service.ProcessorService;

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
		NativeRegExp methodRegex = config.getMethodRegexp();
		return new Jstack(getProcessorService(), config, name(), methodRegex);
	}

}

package org.opwatch.processor.primitives.filter.grep;

import org.opwatch.processor.common.ActiveProcessorFactory;
import org.opwatch.processor.common.ProcessorSignature;
import org.opwatch.service.ProcessorService;
import jdk.nashorn.internal.objects.NativeRegExp;

public class GrepFactory extends ActiveProcessorFactory<GrepConfig, Grep> {

	public GrepFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "grep";
	}

	@Override
	public Class<GrepConfig> configurationClass() {
		return GrepConfig.class;
	}

	@Override
	public Class<Grep> processorClass() {
		return Grep.class;
	}

	@Override
	public ProcessorSignature staticSignature() {
		return ProcessorSignature.makeFilter();
	}

	@Override
	public Grep make(Object scriptObject) {
		GrepConfig config = convertProcessorConfig(scriptObject);
		NativeRegExp regexp = checkPropertyValue("regexp", config.getRegexp());
		boolean invert = checkPropertyValue("invert", config.getInvert());
		return new Grep(getProcessorService(), config, name(), regexp, invert);
	}

}

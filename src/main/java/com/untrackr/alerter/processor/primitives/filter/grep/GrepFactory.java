package com.untrackr.alerter.processor.primitives.filter.grep;

import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.processor.common.ProcessorSignature;
import com.untrackr.alerter.service.ProcessorService;
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
		GrepConfig descriptor = convertProcessorDescriptor(scriptObject);
		NativeRegExp regexp = checkPropertyValue("regexp", descriptor.getRegexp());
		boolean invert = checkPropertyValue("invert", descriptor.getInvert());
		return new Grep(getProcessorService(), descriptor, name(), regexp, invert);
	}

}

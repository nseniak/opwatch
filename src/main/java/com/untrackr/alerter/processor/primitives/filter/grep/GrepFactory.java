package com.untrackr.alerter.processor.primitives.filter.grep;

import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.service.ProcessorService;
import jdk.nashorn.internal.objects.NativeRegExp;

public class GrepFactory extends ActiveProcessorFactory<GrepDescriptor, Grep> {

	public GrepFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "grep";
	}

	@Override
	public Class<GrepDescriptor> descriptorClass() {
		return GrepDescriptor.class;
	}

	@Override
	public Grep make(Object scriptObject) {
		GrepDescriptor descriptor = convertProcessorDescriptor(scriptObject);
		NativeRegExp regexp = checkPropertyValue("regexp", descriptor.getRegexp());
		boolean invert = descriptor.getInvert();
		Grep grep = new Grep(getProcessorService(), descriptor, name(), regexp, invert);
		return grep;
	}

}

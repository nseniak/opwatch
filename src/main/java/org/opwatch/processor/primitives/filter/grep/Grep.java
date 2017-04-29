package org.opwatch.processor.primitives.filter.grep;

import org.opwatch.processor.payload.Payload;
import org.opwatch.processor.primitives.filter.ConditionalFilter;
import org.opwatch.service.ProcessorService;
import jdk.nashorn.internal.objects.NativeRegExp;

public class Grep extends ConditionalFilter<GrepConfig> {

	private NativeRegExp regexp;
	private boolean invert;

	public Grep(ProcessorService processorService, GrepConfig configuration, String name, NativeRegExp regexp, boolean invert) {
		super(processorService, configuration, name);
		this.regexp = regexp;
		this.invert = invert;
	}

	@Override
	public boolean predicateValue(Payload input) {
		String text = payloadValue(input, String.class);
		boolean match = regexp.test(text);
		return (match && !invert) || (!match && invert);
	}

}

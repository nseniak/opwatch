package com.untrackr.alerter.processor.primitives.filter.grep;

import com.untrackr.alerter.processor.payload.Payload;
import com.untrackr.alerter.processor.primitives.filter.ConditionalFilter;
import com.untrackr.alerter.service.ProcessorService;

import java.util.regex.Pattern;

public class Grep extends ConditionalFilter<GrepDescriptor> {

	private Pattern pattern;
	private boolean invert;

	public Grep(ProcessorService processorService, GrepDescriptor descriptor, String name, Pattern pattern, boolean invert) {
		super(processorService, descriptor, name);
		this.pattern = pattern;
		this.invert = invert;
	}

	@Override
	public boolean predicateValue(Payload input) {
		String text = payloadValue(input, String.class);
		boolean match = pattern.matcher(text).find();
		return (match && !invert) || (!match && invert);
	}

}

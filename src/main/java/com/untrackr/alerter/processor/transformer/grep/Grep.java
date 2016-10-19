package com.untrackr.alerter.processor.transformer.grep;

import com.untrackr.alerter.processor.common.Payload;
import com.untrackr.alerter.processor.transformer.ConditionalTransformer;
import com.untrackr.alerter.service.ProcessorService;

import java.util.regex.Pattern;

public class Grep extends ConditionalTransformer {

	private String fieldName;
	private Pattern pattern;
	private boolean invert;

	public Grep(ProcessorService processorService, String name, String fieldName, Pattern pattern, boolean invert) {
		super(processorService, name);
		this.fieldName = fieldName;
		this.pattern = pattern;
		this.invert = invert;
	}

	@Override
	public boolean predicateValue(Payload input) {
		String text = payloadPropertyValue(input, fieldName, String.class);
		boolean match = pattern.matcher(text).find();
		return (match && !invert) || (!match && invert);
	}

}

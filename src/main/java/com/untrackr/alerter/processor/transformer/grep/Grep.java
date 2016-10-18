package com.untrackr.alerter.processor.transformer.grep;

import com.untrackr.alerter.processor.common.ScriptStack;
import com.untrackr.alerter.processor.common.Payload;
import com.untrackr.alerter.processor.common.ProcessorExecutionException;
import com.untrackr.alerter.processor.transformer.ConditionalTransformer;
import com.untrackr.alerter.service.ProcessorService;

import java.util.regex.Pattern;

public class Grep extends ConditionalTransformer {

	private String fieldName;
	private Pattern pattern;
	private boolean invert;
	private boolean fieldErrorSignaled = false;

	public Grep(ProcessorService processorService, ScriptStack stack, String fieldName, Pattern pattern, boolean invert) {
		super(processorService, stack);
		this.fieldName = fieldName;
		this.pattern = pattern;
		this.invert = invert;
	}

	@Override
	public boolean predicateValue(Payload input) {
		String text;
		try {
			text = payloadFieldValue(input, fieldName, String.class);
		} catch (ProcessorExecutionException e) {
			if (fieldErrorSignaled) {
				return false;
			} else {
				fieldErrorSignaled = true;
				throw e;
			}
		}
		boolean match = pattern.matcher(text).find();
		return (match && !invert) || (!match && invert);
	}

	@Override
	public String identifier() {
		return pattern.pattern();
	}

}

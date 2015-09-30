package com.untrackr.alerter.processor.filter.grep;

import com.untrackr.alerter.processor.common.IncludePath;
import com.untrackr.alerter.processor.common.Payload;
import com.untrackr.alerter.processor.common.RuntimeProcessorError;
import com.untrackr.alerter.processor.filter.ConditionalFilter;
import com.untrackr.alerter.service.ProcessorService;

import java.util.regex.Pattern;

public class Grep extends ConditionalFilter {

	private String fieldName;
	private Pattern pattern;
	private boolean invert;
	private boolean fieldErrorSignaled = false;

	public Grep(ProcessorService processorService, IncludePath path, String fieldName, Pattern pattern, boolean invert) {
		super(processorService, path);
		this.fieldName = fieldName;
		this.pattern = pattern;
		this.invert = invert;
	}

	@Override
	public boolean conditionValue(Payload input) {
		String text;
		try {
			text = payloadFieldValue(input, fieldName, String.class);
		} catch (RuntimeProcessorError e) {
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

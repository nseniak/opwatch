package com.untrackr.alerter.processor.filter;

import com.untrackr.alerter.model.descriptor.IncludePath;
import com.untrackr.alerter.processor.common.Payload;
import com.untrackr.alerter.processor.common.RuntimeProcessorError;
import com.untrackr.alerter.service.ProcessorService;

import java.util.regex.Pattern;

public class Grep extends ConditionalFilter {

	private String fieldName;
	private Pattern pattern;
	private boolean invert;

	public Grep(ProcessorService processorService, IncludePath path, String fieldName, Pattern pattern, boolean invert) {
		super(processorService, path);
		this.fieldName = fieldName;
		this.pattern = pattern;
		this.invert = invert;
	}

	@Override
	public boolean conditionValue(Payload input) {
		Object textValue = input.getJsonObject().get(fieldName);
		if (textValue == null) {
			throw new RuntimeProcessorError("field is missing: " + fieldName, this, input);
		}
		if (!(textValue instanceof String)) {
			throw new RuntimeProcessorError("field value expected as string: " + fieldName, this, input);
		}
		String text = (String) textValue;
		boolean match = pattern.matcher(text).find();
		return (match && !invert) || (!match && invert);
	}

	@Override
	public String identifier() {
		return pattern.pattern();
	}

}

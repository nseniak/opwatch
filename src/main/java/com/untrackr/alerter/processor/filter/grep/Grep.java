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
	private boolean missingFieldErrorSignaled = false;
	private boolean wrongFieldValueErrorSignaled = false;

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
			if (missingFieldErrorSignaled) {
				return false;
			} else {
				missingFieldErrorSignaled = true;
				throw new RuntimeProcessorError("field is missing: " + fieldName, this, input);
			}
		}
		if (!(textValue instanceof String)) {
			if (wrongFieldValueErrorSignaled) {
				return false;
			} else {
				wrongFieldValueErrorSignaled = true;
				throw new RuntimeProcessorError("field value expected as string: " + fieldName, this, input);
			}
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

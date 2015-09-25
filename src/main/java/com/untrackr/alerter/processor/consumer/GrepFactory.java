package com.untrackr.alerter.processor.consumer;

import com.untrackr.alerter.model.common.JsonObject;
import com.untrackr.alerter.model.descriptor.GrepDesc;
import com.untrackr.alerter.model.descriptor.IncludePath;
import com.untrackr.alerter.processor.common.Factory;
import com.untrackr.alerter.processor.common.ValidationError;
import com.untrackr.alerter.processor.filter.Grep;
import com.untrackr.alerter.service.ProcessorService;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class GrepFactory extends Factory {

	public GrepFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String type() {
		return "grep";
	}

	@Override
	public Grep make(JsonObject jsonObject, IncludePath path) throws ValidationError {
		GrepDesc descriptor = convertDescriptor(path, GrepDesc.class, jsonObject);
		String fieldName = optionalFieldValue(descriptor, "field", descriptor.getField(), "text");
		String regex = fieldValue(path, jsonObject, "regex", descriptor.getRegex());
		Pattern pattern = compilePattern(path, jsonObject, "regex", regex);
		boolean invert = optionalFieldValue(descriptor, "invert", descriptor.getInvert(), Boolean.FALSE);
		return new Grep(getProcessorService(), path, fieldName, pattern, invert);
	}

	private Pattern compilePattern(IncludePath path, JsonObject description, String field, String regex) throws ValidationError {
		try {
			return Pattern.compile(regex);
		} catch (PatternSyntaxException e) {
			throw new ValidationError(e, path, description);
		}
	}

}

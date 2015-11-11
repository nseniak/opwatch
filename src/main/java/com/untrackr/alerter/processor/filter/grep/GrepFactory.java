package com.untrackr.alerter.processor.filter.grep;

import com.untrackr.alerter.model.common.JsonDescriptor;
import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.processor.common.IncludePath;
import com.untrackr.alerter.processor.common.ValidationError;
import com.untrackr.alerter.service.ProcessorService;

import java.util.regex.Pattern;

public class GrepFactory extends ActiveProcessorFactory {

	public GrepFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String type() {
		return "grep";
	}

	@Override
	public Grep make(JsonDescriptor jsonDescriptor, IncludePath path) throws ValidationError {
		GrepDesc descriptor = convertDescriptor(path, GrepDesc.class, jsonDescriptor);
		String fieldName = optionalFieldValue(path, jsonDescriptor, "field", descriptor.getField(), "text");
		String regex = checkFieldValue(path, jsonDescriptor, "regex", descriptor.getRegex());
		Pattern pattern = compilePattern(path, jsonDescriptor, "regex", regex);
		boolean invert = optionalFieldValue(path, jsonDescriptor, "invert", descriptor.getInvert(), Boolean.FALSE);
		Grep grep = new Grep(getProcessorService(), path, fieldName, pattern, invert);
		initialize(grep, descriptor);
		return grep;
	}

}

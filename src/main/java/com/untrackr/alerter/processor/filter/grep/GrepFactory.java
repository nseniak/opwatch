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
		String[] regexes = optionalFieldValue(path, jsonDescriptor, "regexes", descriptor.getRegexes(), null);
		String regex = optionalFieldValue(path, jsonDescriptor, "regex", descriptor.getRegex(), null);
		if ((regex != null) && (regexes != null)) {
			throw new ValidationError("cannot have both \"regex\" and \"regexes\" defined in " + type(), path, jsonDescriptor);
		}
		Pattern pattern = null;
		if (regex != null) {
			pattern = compilePattern(path, jsonDescriptor, "regex", regex);
		}
		if (regexes != null) {
			StringBuilder builder = new StringBuilder();
			String delimiter = "";
			for (String alternative : regexes) {
				// Compile the aternative, just to check the syntax
				compilePattern(path, jsonDescriptor, "regexes", alternative);
				builder.append(delimiter).append("(?:").append(alternative).append(")");
				delimiter = "|";
			}
			pattern = compilePattern(path, jsonDescriptor, "regexes", builder.toString());
		}
		if (pattern == null) {
			throw new ValidationError("missing " + type() + " field: must define either \"regex\" or \"regexes\"", path, jsonDescriptor);
		}
		boolean invert = optionalFieldValue(path, jsonDescriptor, "invert", descriptor.getInvert(), Boolean.FALSE);
		Grep grep = new Grep(getProcessorService(), path, fieldName, pattern, invert);
		initialize(grep, descriptor);
		return grep;
	}

}

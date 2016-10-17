package com.untrackr.alerter.processor.filter.grep;

import com.untrackr.alerter.model.common.JsonDescriptor;
import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.common.ScriptStack;
import com.untrackr.alerter.processor.common.ValidationError;
import com.untrackr.alerter.service.ProcessorService;

import java.util.List;
import java.util.regex.Pattern;

public class GrepFactory extends ActiveProcessorFactory {

	public GrepFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "grep";
	}

	@Override
	public Processor make(Object object) throws ValidationError {
		JsonDescriptor scriptDescriptor = scriptDescriptor(object);
		GrepDesc descriptor = convertScriptDescriptor(GrepDesc.class, scriptDescriptor);
		String fieldName = optionalFieldValue(scriptDescriptor, "field", descriptor.getField(), "text");
		List<String> regexes = optionalFieldValue(scriptDescriptor, "regexes", descriptor.getRegexes(), null);
		String regex = optionalFieldValue(scriptDescriptor, "regex", descriptor.getRegex(), null);
		if ((regex != null) && (regexes != null)) {
			throw new ValidationError("cannot have both \"regex\" and \"regexes\" defined in " + name(), scriptDescriptor);
		}
		Pattern pattern = null;
		if (regex != null) {
			pattern = compilePattern(scriptDescriptor, "regex", regex);
		}
		if (regexes != null) {
			StringBuilder builder = new StringBuilder();
			String delimiter = "";
			for (String alternative : regexes) {
				// Compile the aternative, just to check the syntax
				compilePattern(scriptDescriptor, "regexes", alternative);
				builder.append(delimiter).append("(?:").append(alternative).append(")");
				delimiter = "|";
			}
			pattern = compilePattern(scriptDescriptor, "regexes", builder.toString());
		}
		if (pattern == null) {
			throw new ValidationError("missing " + name() + " field: must define either \"regex\" or \"regexes\"", scriptDescriptor);
		}
		boolean invert = optionalFieldValue(scriptDescriptor, "invert", descriptor.getInvert(), Boolean.FALSE);
		Grep grep = new Grep(getProcessorService(), ScriptStack.currentStack(), fieldName, pattern, invert);
		initialize(grep, descriptor);
		return grep;
	}

}

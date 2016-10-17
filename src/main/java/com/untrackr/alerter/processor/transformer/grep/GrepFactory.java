package com.untrackr.alerter.processor.transformer.grep;

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
	public Processor make(Object scriptObject) throws ValidationError {
		GrepDesc descriptor = convertProcessorArgument(GrepDesc.class, scriptObject);
		String fieldName = optionalFieldValue("field", descriptor.getField(), "text");
		List<String> regexes = optionalFieldValue("regexes", descriptor.getRegexes(), null);
		String regex = optionalFieldValue("regex", descriptor.getRegex(), null);
		if ((regex != null) && (regexes != null)) {
			throw new ValidationError(name() + ": cannot have both \"regex\" and \"regexes\" defined");
		}
		Pattern pattern = null;
		if (regex != null) {
			pattern = compilePattern("regex", regex);
		}
		if (regexes != null) {
			StringBuilder builder = new StringBuilder();
			String delimiter = "";
			for (String alternative : regexes) {
				// Compile the aternative, just to check the syntax
				compilePattern("regexes", alternative);
				builder.append(delimiter).append("(?:").append(alternative).append(")");
				delimiter = "|";
			}
			pattern = compilePattern("regexes", builder.toString());
		}
		if (pattern == null) {
			throw new ValidationError(name() + ": either \"regex\" or \"regexes\" must be defined");
		}
		boolean invert = optionalFieldValue("invert", descriptor.getInvert(), Boolean.FALSE);
		Grep grep = new Grep(getProcessorService(), ScriptStack.currentStack(), fieldName, pattern, invert);
		initialize(grep, descriptor);
		return grep;
	}

}

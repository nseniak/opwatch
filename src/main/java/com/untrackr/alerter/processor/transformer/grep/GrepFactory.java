package com.untrackr.alerter.processor.transformer.grep;

import com.untrackr.alerter.processor.common.*;
import com.untrackr.alerter.service.ProcessorService;

import java.util.List;
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
	public Grep make(Object scriptObject) {
		GrepDesc descriptor = convertProcessorDescriptor(GrepDesc.class, scriptObject);
		String fieldName = optionaPropertyValue("field", descriptor.getField(), "text");
		List<String> regexes = optionaPropertyValue("regexes", descriptor.getRegexes(), null);
		String regex = optionaPropertyValue("regex", descriptor.getRegex(), null);
		if ((regex != null) && (regexes != null)) {
			throw new AlerterException("cannot have both \"regex\" and \"regexes\" defined", ExceptionContext.makeProcessorFactory(type()));
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
			throw new AlerterException("either \"regex\" or \"regexes\" must be defined", ExceptionContext.makeProcessorFactory(type()));
		}
		boolean invert = optionaPropertyValue("invert", descriptor.getInvert(), Boolean.FALSE);
		Grep grep = new Grep(getProcessorService(), descriptor, type(), fieldName, pattern, invert);
		return grep;
	}

}

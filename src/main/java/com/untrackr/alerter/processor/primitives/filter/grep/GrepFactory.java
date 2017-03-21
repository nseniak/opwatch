package com.untrackr.alerter.processor.primitives.filter.grep;

import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.processor.common.AlerterException;
import com.untrackr.alerter.processor.common.ExceptionContext;
import com.untrackr.alerter.service.ProcessorService;

import java.util.List;
import java.util.regex.Pattern;

public class GrepFactory extends ActiveProcessorFactory<GrepDescriptor, Grep> {

	public GrepFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "grep";
	}

	@Override
	public Class<GrepDescriptor> descriptorClass() {
		return GrepDescriptor.class;
	}

	@Override
	public Grep make(Object scriptObject) {
		GrepDescriptor descriptor = convertProcessorDescriptor(scriptObject);
		List<String> regexes = descriptor.getRegexes();
		String regex = descriptor.getRegex();
		if ((regex != null) && (regexes != null)) {
			throw new AlerterException("either \"regex\" or \"regexes\" must be defined", ExceptionContext.makeProcessorFactory(name()));
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
			throw new AlerterException("either \"regex\" or \"regexes\" must be defined", ExceptionContext.makeProcessorFactory(name()));
		}
		boolean invert = descriptor.getInvert();
		Grep grep = new Grep(getProcessorService(), descriptor, name(), pattern, invert);
		return grep;
	}

}

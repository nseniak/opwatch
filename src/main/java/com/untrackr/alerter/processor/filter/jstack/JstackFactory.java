package com.untrackr.alerter.processor.filter.jstack;

import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.common.ScriptStack;
import com.untrackr.alerter.processor.common.ValidationError;
import com.untrackr.alerter.service.ProcessorService;

import java.util.regex.Pattern;

public class JstackFactory extends ActiveProcessorFactory {

	public JstackFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "jstack";
	}

	@Override
	public Processor make(Object scriptObject) throws ValidationError {
		JstackDesc descriptor = convertProcessorArgument(JstackDesc.class, scriptObject);
		String fieldName = optionalFieldValue("field", descriptor.getField(), "text");
		String methodRegex = optionalFieldValue("methodRegex", descriptor.getMethodRegex(), null);
		Pattern methodPattern = (methodRegex == null) ? null : compilePattern("regex", methodRegex);
		Jstack jstack = new Jstack(getProcessorService(), ScriptStack.currentStack(), fieldName, methodPattern);
		initialize(jstack, descriptor);
		return jstack;
	}

}

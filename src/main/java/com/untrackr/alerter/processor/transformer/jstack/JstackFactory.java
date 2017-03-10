package com.untrackr.alerter.processor.transformer.jstack;

import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.processor.consumer.alert.AlertGeneratorDesc;
import com.untrackr.alerter.service.ProcessorService;

import java.util.regex.Pattern;

public class JstackFactory extends ActiveProcessorFactory {

	public JstackFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String type() {
		return "jstack";
	}

	@Override
	public Class<JstackDesc> descriptorClass() {
		return JstackDesc.class;
	}

	@Override
	public Jstack make(Object scriptObject) {
		JstackDesc descriptor = convertProcessorDescriptor(JstackDesc.class, scriptObject);
		String fieldName = optionaPropertyValue("field", descriptor.getField(), "text");
		String methodRegex = optionaPropertyValue("methodRegex", descriptor.getMethodRegex(), null);
		Pattern methodPattern = (methodRegex == null) ? null : compilePattern("regex", methodRegex);
		Jstack jstack = new Jstack(getProcessorService(), descriptor, type(), fieldName, methodPattern);
		return jstack;
	}

}

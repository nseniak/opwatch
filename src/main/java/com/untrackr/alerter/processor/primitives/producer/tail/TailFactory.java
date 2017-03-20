package com.untrackr.alerter.processor.primitives.producer.tail;

import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.service.ProcessorService;

import java.nio.file.FileSystems;

public class TailFactory extends ActiveProcessorFactory<TailDescriptor, Tail> {

	public TailFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "tail";
	}

	@Override
	public Class<TailDescriptor> descriptorClass() {
		return TailDescriptor.class;
	}

	@Override
	public Tail make(Object scriptObject) {
		TailDescriptor descriptor = convertProcessorDescriptor(scriptObject);
		String file = checkVariableSubstitution("file", checkPropertyValue("file", descriptor.getFile()));
		boolean ignoreBlankLine = optionalPropertyValue("ignoreBlankLine", descriptor.getIgnoreBlankLine(), false);
		Tail tail = new Tail(getProcessorService(), descriptor, name(), FileSystems.getDefault().getPath(file), ignoreBlankLine);
		return tail;
	}

}

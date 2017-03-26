package com.untrackr.alerter.processor.primitives.producer.tail;

import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.service.ProcessorService;

import java.nio.file.FileSystems;

public class TailFactory extends ActiveProcessorFactory<TailConfig, Tail> {

	public TailFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "tail";
	}

	@Override
	public Class<TailConfig> descriptorClass() {
		return TailConfig.class;
	}

	@Override
	public Tail make(Object scriptObject) {
		TailConfig descriptor = convertProcessorDescriptor(scriptObject);
		String file = checkVariableSubstitution("file", checkPropertyValue("file", descriptor.getFile()));
		boolean ignoreBlankLine = optionalPropertyValue("ignoreBlankLine", descriptor.getIgnoreBlankLine(), false);
		Tail tail = new Tail(getProcessorService(), descriptor, name(), FileSystems.getDefault().getPath(file), ignoreBlankLine);
		return tail;
	}

}

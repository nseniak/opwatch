package com.untrackr.alerter.processor.producer.tail;

import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.service.ProcessorService;

import java.nio.file.FileSystems;

public class TailFactory extends ActiveProcessorFactory {

	public TailFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "tail";
	}

	@Override
	public Tail make(Object scriptObject) {
		TailDesc descriptor = convertProcessorArgument(TailDesc.class, scriptObject);
		String file = checkVariableSubstitution("file", checkPropertyValue("file", descriptor.getFile()));
		boolean ignoreBlankLine = optionaPropertyValue("insecure", descriptor.isIgnoreBlankLine(), false);
		Tail tail = new Tail(getProcessorService(), displayName(descriptor), FileSystems.getDefault().getPath(file), ignoreBlankLine);
		return tail;
	}

}

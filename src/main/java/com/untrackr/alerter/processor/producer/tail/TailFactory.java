package com.untrackr.alerter.processor.producer.tail;

import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.service.ProcessorService;

import java.nio.file.FileSystems;

public class TailFactory extends ActiveProcessorFactory {

	public TailFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String type() {
		return "tail";
	}

	@Override
	public Tail make(Object scriptObject) {
		TailDesc descriptor = convertProcessorDescriptor(TailDesc.class, scriptObject);
		String file = checkVariableSubstitution("file", checkPropertyValue("file", descriptor.getFile()));
		boolean json = optionaPropertyValue("json", descriptor.getJson(), false);
		boolean ignoreBlankLine = optionaPropertyValue("ignoreBlankLine", descriptor.getIgnoreBlankLine(), false);
		Tail tail = new Tail(getProcessorService(), descriptor, type(), FileSystems.getDefault().getPath(file), json, ignoreBlankLine);
		return tail;
	}

}

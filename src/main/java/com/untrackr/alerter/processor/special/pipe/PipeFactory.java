package com.untrackr.alerter.processor.special.pipe;

import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.common.ProcessorFactory;
import com.untrackr.alerter.service.ProcessorService;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import java.util.List;

public class PipeFactory extends ProcessorFactory {

	public PipeFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "pipe";
	}

	@Override
	public Pipe make(Object scriptObject) {
		List<Processor> processors;
		String name;
		if ((scriptObject instanceof ScriptObjectMirror) && (((ScriptObjectMirror) scriptObject).isArray())) {
			processors = (List<Processor>) convertProcessorArgument(List.class, processorListType(), scriptObject);
			name = name();
		} else {
			PipeDesc descriptor = convertProcessorArgument(PipeDesc.class, scriptObject);
			processors = descriptor.getProcessors();
			name = displayName(descriptor);
		}
		return new Pipe(getProcessorService(), processors, name);
	}

}

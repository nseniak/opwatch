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
	public String type() {
		return "pipe";
	}

	@Override
	public Pipe make(Object scriptObject) {
		List<Processor> processors;
		PipeDesc descriptor;
		String name;
		if ((scriptObject instanceof ScriptObjectMirror) && (((ScriptObjectMirror) scriptObject).isArray())) {
			processors = (List<Processor>) convertProcessorArgument(List.class, processorListType(), scriptObject);
			descriptor = new PipeDesc();
			descriptor.setName(type());
			descriptor.setProcessors(processors);
			name = type();
		} else {
			descriptor = convertProcessorArgument(PipeDesc.class, scriptObject);
			processors = descriptor.getProcessors();
			name = type();
		}
		return new Pipe(getProcessorService(), processors, descriptor, name);
	}

}

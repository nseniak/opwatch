package com.untrackr.alerter.processor.special.parallel;

import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.common.ProcessorFactory;
import com.untrackr.alerter.service.ProcessorService;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import java.util.List;

public class ParallelFactory extends ProcessorFactory {

	public ParallelFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String type() {
		return "parallel";
	}

	@Override
	public Parallel make(Object scriptObject) {
		List<Processor> processors;
		ParallelDesc descriptor;
		String name;
		if ((scriptObject instanceof ScriptObjectMirror) && (((ScriptObjectMirror) scriptObject).isArray())) {
			processors = (List<Processor>) convertProcessorArgument(List.class, processorListType(), scriptObject);
			descriptor = new ParallelDesc();
			descriptor.setName(type());
			descriptor.setProcessors(processors);
			name = type();
		} else {
			descriptor = convertProcessorArgument(ParallelDesc.class, scriptObject);
			processors = descriptor.getProcessors();
			name = type();
		}
		Parallel parallel = new Parallel(getProcessorService(), processors, descriptor, name);
		parallel.inferSignature();
		return parallel;
	}

}

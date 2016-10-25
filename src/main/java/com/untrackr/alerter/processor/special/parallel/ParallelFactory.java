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
	public String name() {
		return "parallel";
	}

	@Override
	public Parallel make(Object scriptObject) {
		List<Processor> processors;
		String name;
		if ((scriptObject instanceof ScriptObjectMirror) && (((ScriptObjectMirror) scriptObject).isArray())) {
			processors = (List<Processor>) convertProcessorArgument(List.class, processorListType(), scriptObject);
			name = name();
		} else {
			ParallelDesc descriptor = convertProcessorArgument(ParallelDesc.class, scriptObject);
			processors = descriptor.getProcessors();
			name = displayName(descriptor);
		}
		Parallel parallel = new Parallel(getProcessorService(), processors, name);
		parallel.inferSignature();
		return parallel;
	}

}

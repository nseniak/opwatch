package com.untrackr.alerter.processor.special.pipe;

import com.untrackr.alerter.model.common.JsonDescriptor;
import com.untrackr.alerter.processor.common.IncludePath;
import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.common.ProcessorFactory;
import com.untrackr.alerter.processor.common.ValidationError;
import com.untrackr.alerter.processor.filter.identity.Identity;
import com.untrackr.alerter.service.FactoryService;
import com.untrackr.alerter.service.ProcessorService;

import java.io.IOException;
import java.util.ArrayList;
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
	public Processor make(JsonDescriptor jsonDescriptor, IncludePath path) throws ValidationError, IOException {
		PipeDesc descriptor = convertDescriptor(path, PipeDesc.class, jsonDescriptor);
		List<JsonDescriptor> processorDescs = descriptor.getPipe();
		int size = processorDescs.size();
		if (size == 0) {
			return new Identity(getProcessorService(), path);
		}
		FactoryService factoryService = getProcessorService().getFactoryService();
		List<Processor> processors = new ArrayList<>();
		for (JsonDescriptor processorDesc : processorDescs) {
			processors.add(factoryService.makeProcessor(processorDesc, path));
		}
		return new Pipe(getProcessorService(), processors, path);
	}

}

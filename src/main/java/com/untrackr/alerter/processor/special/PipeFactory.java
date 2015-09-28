package com.untrackr.alerter.processor.special;

import com.untrackr.alerter.model.common.JsonObject;
import com.untrackr.alerter.model.descriptor.IncludePath;
import com.untrackr.alerter.model.descriptor.PipeDesc;
import com.untrackr.alerter.processor.common.ProcessorFactory;
import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.common.ValidationError;
import com.untrackr.alerter.processor.filter.Identity;
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
	public Processor make(JsonObject jsonObject, IncludePath path) throws ValidationError, IOException {
		PipeDesc descriptor = convertDescriptor(path, PipeDesc.class, jsonObject);
		List<JsonObject> processorDescs = descriptor.getPipe();
		int size = processorDescs.size();
		if (size == 0) {
			return new Identity(getProcessorService(), path);
		}
		FactoryService factoryService = getProcessorService().getFactoryService();
		List<Processor> processors = new ArrayList<>();
		for (JsonObject processorDesc : processorDescs) {
			processors.add(factoryService.makeProcessor(processorDesc, path));
		}
		return new Pipe(getProcessorService(), processors, path);
	}

}

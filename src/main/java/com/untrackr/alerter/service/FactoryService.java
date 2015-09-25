package com.untrackr.alerter.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.untrackr.alerter.model.common.JsonObject;
import com.untrackr.alerter.model.descriptor.IncludePath;
import com.untrackr.alerter.processor.common.Factory;
import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.common.ValidationError;
import com.untrackr.alerter.processor.consumer.AlertGeneratorFactory;
import com.untrackr.alerter.processor.consumer.GrepFactory;
import com.untrackr.alerter.processor.consumer.JSGrepFactory;
import com.untrackr.alerter.processor.filter.PrintFactory;
import com.untrackr.alerter.processor.producer.StatFactory;
import com.untrackr.alerter.processor.producer.TailFactory;
import com.untrackr.alerter.processor.special.ParallelFactory;
import com.untrackr.alerter.processor.special.PipeFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class FactoryService implements InitializingBean {

	@Autowired
	private ProcessorService processorService;

	private ParallelFactory parallelFactory;
	private PipeFactory pipeFactory;

	private Map<String, Factory> factories = new HashMap<>();

	private ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public void afterPropertiesSet() throws Exception {
		parallelFactory = new ParallelFactory(processorService);
		pipeFactory = new PipeFactory(processorService);
		registerFactory(new GrepFactory(processorService));
		registerFactory(new JSGrepFactory(processorService));
		registerFactory(new TailFactory(processorService));
		registerFactory(new PrintFactory(processorService));
		registerFactory(new StatFactory(processorService));
		registerFactory(new AlertGeneratorFactory(processorService));
	}

	private void registerFactory(Factory factory) {
		factories.put(factory.type(), factory);
	}

	public Processor loadProcessor(String filename) throws ValidationError {
		IncludePath path = new IncludePath().append(filename);
		try {
			return makeProcessor(loadDescriptor(filename), path);
		} catch (IOException e) {
			throw new ValidationError(e, path);
		}
	}

	public JsonObject loadDescriptor(String filename) throws IOException {
		File pathname = processorService.findInPath(filename);
		if (pathname == null) {
			throw new FileNotFoundException(filename);
		}
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
		return mapper.readValue(pathname, JsonObject.class);
	}

	public Processor makeProcessor(JsonObject descriptor, IncludePath path) throws ValidationError {
		try {
			// Include
			if (descriptor.get("include") != null) {
				Object fileObject = descriptor.get("include");
				if (!(fileObject instanceof String)) {
					throw new ValidationError("incorrect filename", path, descriptor);
				}
				String pathname = (String) fileObject;
				JsonObject loadedDescriptor;
				loadedDescriptor = loadDescriptor(pathname);
				return makeProcessor(loadedDescriptor, path.append(pathname));
			}
			// Pipe
			if (descriptor.get("pipe") != null) {
				return pipeFactory.make(descriptor, path);
			}
			// Parallel
			if (descriptor.get("parallel") != null) {
				return parallelFactory.make(descriptor, path);
			}
			// Other processors
			Object typeObject = descriptor.get("processor");
			if (typeObject == null) {
				throw new ValidationError("missing processor name", path, descriptor);
			}
			if (!(typeObject instanceof String)) {
				throw new ValidationError("incorrect processor name", path, descriptor);
			}
			String type = (String) typeObject;
			Factory factory = factories.get(type);
			if (factory == null) {
				throw new ValidationError("unknown processor name: \"" + type + "\"", path, descriptor);
			}
			return factory.make(descriptor, path);
		} catch (ValidationError validationError) {
			throw validationError;
		} catch (Throwable t) {
			throw new ValidationError(t, path);
		}
	}

	public ObjectMapper getObjectMapper() {
		return objectMapper;
	}

}

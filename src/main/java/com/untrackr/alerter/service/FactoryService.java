package com.untrackr.alerter.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.untrackr.alerter.model.common.JsonDescriptor;
import com.untrackr.alerter.processor.common.IncludePath;
import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.common.ProcessorFactory;
import com.untrackr.alerter.processor.common.ValidationError;
import com.untrackr.alerter.processor.consumer.alert.AlertGeneratorFactory;
import com.untrackr.alerter.processor.filter.collect.CollectFactory;
import com.untrackr.alerter.processor.filter.grep.GrepFactory;
import com.untrackr.alerter.processor.filter.js.JSFactory;
import com.untrackr.alerter.processor.filter.jsgrep.JSGrepFactory;
import com.untrackr.alerter.processor.filter.once.OnceFactory;
import com.untrackr.alerter.processor.filter.print.PrintFactory;
import com.untrackr.alerter.processor.producer.console.ConsoleFactory;
import com.untrackr.alerter.processor.producer.count.CountFactory;
import com.untrackr.alerter.processor.producer.curl.CurlFactory;
import com.untrackr.alerter.processor.producer.df.DfFactory;
import com.untrackr.alerter.processor.producer.stat.StatFactory;
import com.untrackr.alerter.processor.producer.tail.TailFactory;
import com.untrackr.alerter.processor.producer.top.TopFactory;
import com.untrackr.alerter.processor.producer.trail.TrailFactory;
import com.untrackr.alerter.processor.special.parallel.ParallelFactory;
import com.untrackr.alerter.processor.special.pipe.PipeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class FactoryService implements InitializingBean {

	private static final Logger logger = LoggerFactory.getLogger(FactoryService.class);

	@Autowired
	private ProcessorService processorService;

	private ParallelFactory parallelFactory;
	private PipeFactory pipeFactory;

	private Map<String, ProcessorFactory> factories = new HashMap<>();

	private ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public void afterPropertiesSet() throws Exception {
		parallelFactory = new ParallelFactory(processorService);
		pipeFactory = new PipeFactory(processorService);
		registerFactory(new ConsoleFactory(processorService));
		registerFactory(new GrepFactory(processorService));
		registerFactory(new JSGrepFactory(processorService));
		registerFactory(new JSFactory(processorService));
		registerFactory(new CollectFactory(processorService));
		registerFactory(new TailFactory(processorService));
		registerFactory(new PrintFactory(processorService));
		registerFactory(new StatFactory(processorService));
		registerFactory(new DfFactory(processorService));
		registerFactory(new TopFactory(processorService));
		registerFactory(new AlertGeneratorFactory(processorService));
		registerFactory(new OnceFactory(processorService));
		registerFactory(new CurlFactory(processorService));
		registerFactory(new CountFactory(processorService));
		registerFactory(new TrailFactory(processorService));
	}

	private void registerFactory(ProcessorFactory processorFactory) {
		factories.put(processorFactory.type(), processorFactory);
	}

	public Processor loadProcessor(String filename, IncludePath path) throws ValidationError {
		IncludePath.LoadedFile file = processorService.findFile(filename, path);
		if (file == null) {
			throw new ValidationError("file not found: " + filename, path, null);
		}

		return makeProcessor(loadDescriptor(file, path), path.append(file));
	}

	public JsonDescriptor loadDescriptor(IncludePath.LoadedFile file, IncludePath path) {
		try {
			logger.info("Loading file: " + file.getFile().toString());
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
			return mapper.readValue(file.getFile(), JsonDescriptor.class);
		} catch (IOException e) {
			throw new ValidationError(e, path.append(file));
		}
	}

	public Processor makeProcessor(JsonDescriptor descriptor, IncludePath path) throws ValidationError {
		try {
			// Include
			if (descriptor.get("include") != null) {
				Object fileObject = descriptor.get("include");
				if (!(fileObject instanceof String)) {
					throw new ValidationError("invalid \"include\" filename", path, descriptor);
				}
				String filename = (String) fileObject;
				return loadProcessor(filename, path);
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
				throw new ValidationError("missing \"processor\" field", path, descriptor);
			}
			if (!(typeObject instanceof String)) {
				throw new ValidationError("incorrect \"processor\" name", path, descriptor);
			}
			String type = (String) typeObject;
			ProcessorFactory processorFactory = factories.get(type);
			if (processorFactory == null) {
				throw new ValidationError("unknown \"processor\" name: \"" + type + "\"", path, descriptor);
			}
			return processorFactory.make(descriptor, path);
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

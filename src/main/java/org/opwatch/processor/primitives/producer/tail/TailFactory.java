package org.opwatch.processor.primitives.producer.tail;

import org.opwatch.processor.common.ActiveProcessorFactory;
import org.opwatch.processor.common.ProcessorSignature;
import org.opwatch.service.ProcessorService;

import java.nio.file.FileSystems;

public class TailFactory extends ActiveProcessorFactory<TailConfig, Tail> {

	public TailFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "tail";
	}

	@Override
	public Class<TailConfig> configurationClass() {
		return TailConfig.class;
	}

	@Override
	public Class<Tail> processorClass() {
		return Tail.class;
	}

	@Override
	public ProcessorSignature staticSignature() {
		return ProcessorSignature.makeProducer();
	}

	@Override
	public Tail make(Object scriptObject) {
		TailConfig config = convertProcessorConfig(scriptObject);
		String file = checkPropertyValue("file", config.getFile());
		boolean ignoreBlankLine = checkPropertyValue("ignoreBlank", config.getIgnoreBlank());
		return new Tail(getProcessorService(), config, name(), FileSystems.getDefault().getPath(file), ignoreBlankLine);
	}

}

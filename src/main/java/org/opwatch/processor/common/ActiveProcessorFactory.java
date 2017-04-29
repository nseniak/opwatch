package org.opwatch.processor.common;

import org.opwatch.processor.config.ActiveProcessorConfig;
import org.opwatch.processor.primitives.producer.CommandExecutorDesc;
import org.opwatch.processor.primitives.producer.CommandRunner;
import org.opwatch.service.ProcessorService;

import java.io.File;

public abstract class ActiveProcessorFactory<D extends ActiveProcessorConfig, P extends ActiveProcessor> extends ProcessorFactory<D, P> {

	public ActiveProcessorFactory(ProcessorService processorService) {
		super(processorService);
	}

	protected CommandRunner makeCommandOutputProducer(CommandExecutorDesc descriptor) {
		String command = checkPropertyValue("command", descriptor.getCommand());
		File directory = new File(".");
		return new CommandRunner(processorService, command, directory);
	}

}

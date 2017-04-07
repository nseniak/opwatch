package com.untrackr.alerter.processor.common;

import com.untrackr.alerter.processor.config.ActiveProcessorConfig;
import com.untrackr.alerter.processor.primitives.producer.CommandExecutorDesc;
import com.untrackr.alerter.processor.primitives.producer.CommandRunner;
import com.untrackr.alerter.service.ProcessorService;

import java.io.File;

public abstract class ActiveProcessorFactory<D extends ActiveProcessorConfig, P extends ActiveProcessor> extends ProcessorFactory<D, P> {

	public ActiveProcessorFactory(ProcessorService processorService) {
		super(processorService);
	}

	protected CommandRunner makeCommandOutputProducer(CommandExecutorDesc descriptor) {
		String command = checkVariableSubstitution("command", checkPropertyValue("command", descriptor.getCommand()));
		File directory = new File(".");
		return new CommandRunner(processorService, command, directory);
	}

}

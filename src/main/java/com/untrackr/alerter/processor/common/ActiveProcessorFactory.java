package com.untrackr.alerter.processor.common;

import com.untrackr.alerter.processor.producer.CommandExecutorDesc;
import com.untrackr.alerter.processor.producer.CommandRunner;
import com.untrackr.alerter.service.ProcessorService;

import java.io.File;

public abstract class ActiveProcessorFactory<D extends ActiveProcessorDesc, P extends ActiveProcessor> extends ProcessorFactory<D, P> {

	public ActiveProcessorFactory(ProcessorService processorService) {
		super(processorService);
	}

	protected CommandRunner makeCommandOutputProducer(CommandExecutorDesc descriptor) {
		String command = checkVariableSubstitution("command", checkPropertyValue("command", descriptor.getGenerator()));
		ScriptStack.ScriptStackElement top = ScriptStack.currentStack().top();
		File directory = new File(".");
		if (top != null) {
			directory = new File(top.getFileName()).getParentFile();
		}
		return new CommandRunner(processorService, command, directory);
	}

}

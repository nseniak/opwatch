package com.untrackr.alerter.processor.common;

import com.untrackr.alerter.processor.producer.CommandExecutorDesc;
import com.untrackr.alerter.processor.producer.CommandRunner;
import com.untrackr.alerter.service.ProcessorService;

public abstract class ActiveProcessorFactory extends ProcessorFactory {

	public ActiveProcessorFactory(ProcessorService processorService) {
		super(processorService);
	}

	protected void initialize(ActiveProcessor processor, ActiveProcessorDesc processorDesc) {
		if (processorDesc.getName() != null) {
			processor.setName(processorDesc.getName());
		}
	}

	protected CommandRunner makeCommandOutputProducer(CommandExecutorDesc descriptor) {
		String command = checkVariableSubstitution("command", checkFieldValue("command", descriptor.getGenerator()));
		return new CommandRunner(processorService, command);
	}

}

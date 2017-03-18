package com.untrackr.alerter.processor.primitives.transformer.sh;

import com.untrackr.alerter.processor.descriptor.ActiveProcessorDescriptor;
import com.untrackr.alerter.processor.primitives.producer.CommandExecutorDesc;

public class ShDescriptor extends ActiveProcessorDescriptor implements CommandExecutorDesc {

	private String command;

	public String getGenerator() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

}

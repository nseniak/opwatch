package com.untrackr.alerter.processor.primitives.filter.sh;

import com.untrackr.alerter.processor.descriptor.ActiveProcessorDescriptor;
import com.untrackr.alerter.processor.descriptor.DefaultOption;
import com.untrackr.alerter.processor.primitives.producer.CommandExecutorDesc;

public class ShDescriptor extends ActiveProcessorDescriptor implements CommandExecutorDesc {

	private String command;

	@DefaultOption
	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

}

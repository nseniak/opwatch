package com.untrackr.alerter.processor.primitives.producer.cron;

import com.untrackr.alerter.processor.descriptor.DefaultOption;
import com.untrackr.alerter.processor.descriptor.ScheduledProcessorDescriptor;
import com.untrackr.alerter.processor.primitives.producer.CommandExecutorDesc;

public class CronDescriptor extends ScheduledProcessorDescriptor implements CommandExecutorDesc {

	private String command;

	@DefaultOption
	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

}

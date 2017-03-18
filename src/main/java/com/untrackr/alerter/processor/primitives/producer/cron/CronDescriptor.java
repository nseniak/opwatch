package com.untrackr.alerter.processor.primitives.producer.cron;

import com.untrackr.alerter.processor.descriptor.ScheduledProducerDescriptor;
import com.untrackr.alerter.processor.primitives.producer.CommandExecutorDesc;

public class CronDescriptor extends ScheduledProducerDescriptor implements CommandExecutorDesc {

	private String command;

	public String getGenerator() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

}

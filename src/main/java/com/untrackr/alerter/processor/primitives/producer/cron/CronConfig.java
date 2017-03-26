package com.untrackr.alerter.processor.primitives.producer.cron;

import com.untrackr.alerter.processor.config.ImplicitProperty;
import com.untrackr.alerter.processor.config.ScheduledProcessorConfig;
import com.untrackr.alerter.processor.primitives.producer.CommandExecutorDesc;

public class CronConfig extends ScheduledProcessorConfig implements CommandExecutorDesc {

	private String command;

	@ImplicitProperty
	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

}

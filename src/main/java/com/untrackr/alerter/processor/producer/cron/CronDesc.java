package com.untrackr.alerter.processor.producer.cron;

import com.untrackr.alerter.processor.common.ScheduledProducerDesc;
import com.untrackr.alerter.processor.producer.CommandExecutorDesc;

public class CronDesc extends ScheduledProducerDesc implements CommandExecutorDesc {

	private String command;

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

}

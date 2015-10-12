package com.untrackr.alerter.processor.filter.sh;

import com.untrackr.alerter.processor.common.ActiveProcessorDesc;
import com.untrackr.alerter.processor.producer.CommandExecutorDesc;

public class ShDesc extends ActiveProcessorDesc implements CommandExecutorDesc {

	private String command;

	@Override
	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

}

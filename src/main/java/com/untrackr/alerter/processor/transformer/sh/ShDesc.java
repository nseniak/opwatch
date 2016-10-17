package com.untrackr.alerter.processor.transformer.sh;

import com.untrackr.alerter.processor.common.ActiveProcessorDesc;
import com.untrackr.alerter.processor.producer.CommandExecutorDesc;

public class ShDesc extends ActiveProcessorDesc implements CommandExecutorDesc {

	private String command;

	public String getGenerator() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

}

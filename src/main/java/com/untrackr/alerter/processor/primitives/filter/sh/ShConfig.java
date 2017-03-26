package com.untrackr.alerter.processor.primitives.filter.sh;

import com.untrackr.alerter.processor.config.ActiveProcessorConfig;
import com.untrackr.alerter.processor.config.ImplicitProperty;
import com.untrackr.alerter.processor.primitives.producer.CommandExecutorDesc;

public class ShConfig extends ActiveProcessorConfig implements CommandExecutorDesc {

	private String command;

	@ImplicitProperty
	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

}

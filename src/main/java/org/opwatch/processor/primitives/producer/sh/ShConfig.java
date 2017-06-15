package org.opwatch.processor.primitives.producer.sh;

import org.opwatch.processor.config.ImplicitProperty;
import org.opwatch.processor.config.ScheduledProcessorConfig;
import org.opwatch.processor.primitives.producer.CommandExecutorDesc;

public class ShConfig extends ScheduledProcessorConfig implements CommandExecutorDesc {

	private String command;

	@ImplicitProperty
	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

}

package org.opwatch.channel.common;

import org.opwatch.processor.common.Message;

public interface Channel {

	String serviceName();

	String name();

	void publish(Message message);

	default String logString() {
		return "[" + serviceName() + " channel \"" + name() + "\"]";
	}

}

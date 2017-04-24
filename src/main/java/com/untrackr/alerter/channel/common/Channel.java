package com.untrackr.alerter.channel.common;

import com.untrackr.alerter.processor.common.Message;

public interface Channel {

	String serviceName();

	String name();

	void publish(Message message);

	default String logString() {
		return "[" + serviceName() + " channel \"" + name() + "\"]";
	}

}

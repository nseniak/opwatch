package com.untrackr.alerter.channel.common;

import com.untrackr.alerter.processor.common.Message;

public interface Channel {

	String serviceName();

	String name();

	void publish(Message message);

}

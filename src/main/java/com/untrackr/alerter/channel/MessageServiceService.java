package com.untrackr.alerter.channel;

import com.untrackr.alerter.channel.common.*;
import com.untrackr.alerter.channel.console.ConsoleConfiguration;
import com.untrackr.alerter.channel.console.ConsoleMessageService;
import com.untrackr.alerter.channel.pushover.PushoverMessageService;
import com.untrackr.alerter.processor.common.RuntimeError;
import com.untrackr.alerter.service.ProcessorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;

import static com.untrackr.alerter.channel.console.ConsoleMessageService.CONSOLE_CHANNEL_NAME;
import static com.untrackr.alerter.channel.console.ConsoleMessageService.CONSOLE_SERVICE_NAME;

@Service
public class MessageServiceService {

	private static final Logger logger = LoggerFactory.getLogger(MessageServiceService.class);



}

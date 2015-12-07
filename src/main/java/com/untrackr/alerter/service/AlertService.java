package com.untrackr.alerter.service;

import com.google.common.collect.EvictingQueue;
import com.untrackr.alerter.model.common.Alert;
import com.untrackr.alerter.model.common.PushoverKey;
import com.untrackr.alerter.model.common.PushoverSettings;
import net.pushover.client.*;
import org.apache.commons.math3.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.StringWriter;
import java.util.concurrent.TimeUnit;

@Service
public class AlertService {

	private static final Logger logger = LoggerFactory.getLogger(AlertService.class);

	@Autowired
	private ProfileService profileService;

	@Autowired
	private ProcessorService processorService;

	private EvictingQueue<Alert> sentAlertQueue;

	private boolean alertQueueFullErrorSignaled = false;
	private PushoverKey pushoverKey;

	public static int MAX_TITLE_LENGTH = 250;
	public static int MAX_MESSAGE_LENGTH = 1024;
	private static final int MAX_DATA_ITEM_LENGTH = 120;
	private static final String FIELD_DELIMITER = "\n--\n";

	@PostConstruct
	public void initializeAlertQueue() {
		sentAlertQueue = EvictingQueue.create(profileService.profile().getMaxAlertsPerMinute());
	}

	@PostConstruct
	public void initializePushoverKey() {
		PushoverSettings settings = profileService.profile().getPushoverSettings();
		String applicationName = profileService.profile().getDefaultPushoverApplication();
		String groupName = profileService.profile().getDefaultPushoverGroup();
		pushoverKey = settings.makeKey(applicationName, groupName);
	}

	public synchronized void alert(Alert alert) {
		alert.setTimestamp(System.currentTimeMillis());
		sentAlertQueue.add(alert);
		int maxPerMinute = profileService.profile().getMaxAlertsPerMinute();
		if (sentAlertQueue.size() == maxPerMinute) {
			long elapsed = System.currentTimeMillis() - sentAlertQueue.peek().getTimestamp();
			if (elapsed <= TimeUnit.MINUTES.toMillis(1)) {
				if (alertQueueFullErrorSignaled) {
					return;
				}
				alertQueueFullErrorSignaled = true;
				send("Max alerts per minute reached on " + processorService.getHostName(), "Muting for a moment.\nMaximum per minute: " + maxPerMinute, MessagePriority.NORMAL, null, null);
				logger.warn("Max alerts per minutes reached: Alert not sent");
				return;
			}
		}
		alertQueueFullErrorSignaled = false;
		MessagePriority priority = MessagePriority.EMERGENCY;
		String prefix = "";
		if (alert.isEnd()) {
			priority = MessagePriority.LOW;
			prefix = "End of Alert: ";
		} else {
			switch (alert.getPriority()) {
				case info:
					priority = MessagePriority.LOW;
					prefix = "Info: ";
					break;
				case normal:
					priority = MessagePriority.NORMAL;
					prefix = "Alert(NORMAL): ";
					break;
				case high:
					priority = MessagePriority.HIGH;
					prefix = "Alert(HIGH): ";
					break;
				case emergency:
					priority = MessagePriority.EMERGENCY;
					prefix = "EMERGENCY: ";
					break;
			}
		}
		Integer retry = null;
		Integer expire = null;
		if (!alert.isEnd()) {
			retry = alert.getRetry();
			expire = alert.getExpire();
			if (priority == MessagePriority.EMERGENCY) {
				if (retry == null) {
					retry = profileService.profile().getDefaultEmergencyRetry();
				}
				if (expire == null) {
					expire = profileService.profile().getDefaultEmergencyExpire();
				}
			}
		}
		String title = truncate(prefix + alert.getTitle(), MAX_TITLE_LENGTH);
		logger.info("Sending alert: " + title);
		StringWriter writer = new StringWriter();
		if (alert.getMessage() != null) {
			writer.append(alert.getMessage()).append(FIELD_DELIMITER);
			logger.info("   message: " + alert.getMessage());
		}
		if (alert.getData() != null) {
			for (Pair<String, String> pair : alert.getData()) {
				String key = pair.getKey();
				String value = pair.getValue();
				logger.info("   " + key + ": " + value);
				writer.append(key).append(": ").append(truncate(value, MAX_DATA_ITEM_LENGTH)).append(FIELD_DELIMITER);
			}
		}
		String message = writer.toString();
		if (message.isEmpty()) {
			message = "--";
		}
		message = truncate(message, MAX_MESSAGE_LENGTH);
		send(title, message, priority, retry, expire);
	}

	private String truncate(String str, int length) {
		if (str.length() <= length) {
			return str;
		} else {
			return str.substring(0, Math.max(0, length - 3)) + "...";
		}
	}

	private void send(String title, String message, MessagePriority priority, Integer retry, Integer expire) {
		if (profileService.profile().isInteractive()) {
			logger.warn("Test mode: Alert not sent");
			return;
		}
		PushoverMessage msg = PushoverMessage.builderWithApiToken(pushoverKey.getApiToken())
				.setUserId(pushoverKey.getUserKey())
				.setTitle(title)
				.setMessage(message)
				.setPriority(priority)
				.setRetry(retry)
				.setExpire(expire)
				.build();
		PushoverClient pushoverClient = new PushoverRestClient();
		try {
			Status result = pushoverClient.pushMessage(msg);
			if (result.getStatus() != 1) {
				logger.error("Status error while pushing to Pushover: " + result.toString());
			}
		} catch (PushoverException e) {
			logger.error("Exception while pushing to Pushover", e);
		}
	}

}

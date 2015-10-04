package com.untrackr.alerter.service;

import com.google.common.collect.EvictingQueue;
import com.untrackr.alerter.model.common.Alert;
import com.untrackr.alerter.model.common.PushoverKey;
import net.pushover.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
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

	@PostConstruct
	public void initializeAlertQueue() {
		sentAlertQueue = EvictingQueue.create(profileService.profile().getMaxAlertsPerMinute());
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
		logger.info("Sending alert: " + alert.toString());
		MessagePriority priority = MessagePriority.EMERGENCY;
		String prefix = "";
		switch (alert.getPriority()) {
			case low:
				priority = MessagePriority.LOW;
				prefix = "";
				break;
			case normal:
				priority = MessagePriority.NORMAL;
				prefix = "Alert: ";
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
		Integer retry = alert.getRetry();
		Integer expire = alert.getExpire();
		if (priority == MessagePriority.EMERGENCY) {
			if (retry == null) {
				retry = profileService.profile().getDefaultEmergencyRetry();
			}
			if (expire == null) {
				expire = profileService.profile().getDefaultEmergencyExpire();
			}
		}
		String message = !alert.getMessage().trim().isEmpty() ? alert.getMessage() : "--";
		String title = prefix + alert.getTitle();
		send(title, message, priority, retry, expire);
	}

	private void send(String title, String message, MessagePriority priority, Integer retry, Integer expire) {
		if (profileService.profile().isInteractive()) {
			logger.warn("Test mode: Alert not sent");
			return;
		}
		PushoverKey key = profileService.profile().getPushoverKey();
		PushoverMessage msg = PushoverMessage.builderWithApiToken(key.getApiToken())
				.setUserId(key.getUserId())
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

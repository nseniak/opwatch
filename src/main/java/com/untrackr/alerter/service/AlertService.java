package com.untrackr.alerter.service;

import com.google.common.base.Strings;
import com.untrackr.alerter.model.common.Alert;
import com.untrackr.alerter.model.common.PushoverKey;
import net.pushover.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AlertService {

	private static final Logger logger = LoggerFactory.getLogger(AlertService.class);

	@Autowired
	private ProfileService profileService;

	public synchronized void alert(Alert alert) {
		logger.info("Sending alert: " + alert.toString());
		if (profileService.profile().isTestMode()) {
			logger.warn("Alert not sent");
			return;
		}
		MessagePriority priority = MessagePriority.EMERGENCY;
		switch (alert.getPriority()) {
			case low:
				priority = MessagePriority.LOW;
				break;
			case normal:
				priority = MessagePriority.NORMAL;
				break;
			case high:
				priority = MessagePriority.HIGH;
				break;
			case emergency:
				priority = MessagePriority.EMERGENCY;
				break;
		}
		PushoverKey key = profileService.profile().getPushoverKey();
		Integer retry = alert.getRetry();
		Integer expire = alert.getExpire();
		if (priority == MessagePriority.EMERGENCY) {
			if (retry == null) {
				retry = profileService.profile().getDefaultEmergencyRetry();
				expire = profileService.profile().getDefaultEmergencyExpire();
			}
		}
		String message = !Strings.isNullOrEmpty(alert.getMessage()) ? alert.getMessage() : " ";
		PushoverMessage msg = PushoverMessage.builderWithApiToken(key.getApiToken())
				.setUserId(key.getUserId())
				.setTitle(alert.getPriority().name() + ": " + alert.getTitle())
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

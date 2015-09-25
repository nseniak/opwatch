package com.untrackr.alerter.service;

import com.untrackr.alerter.model.common.Alert;
import com.untrackr.alerter.model.common.PushoverKey;
import com.untrackr.alerter.processor.common.Payload;
import com.untrackr.alerter.processor.common.Processor;
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

	public void alert(Alert alert) {
		logger.info("Sending alert: " + alert.toString());
		if (profileService.profile().isNoAlarms()) {
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
		PushoverMessage msg = PushoverMessage.builderWithApiToken(key.getApiToken())
				.setUserId(key.getUserId())
				.setTitle(alert.getPriority().name() + ": " + alert.getTitle())
				.setMessage(alert.getMessage())
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

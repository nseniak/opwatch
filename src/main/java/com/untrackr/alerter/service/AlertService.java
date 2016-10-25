package com.untrackr.alerter.service;

import com.untrackr.alerter.model.common.Alert;
import com.untrackr.alerter.model.common.PushoverKey;
import com.untrackr.alerter.model.common.PushoverSettings;
import com.untrackr.alerter.processor.common.FrequencyLimiter;
import com.untrackr.alerter.processor.common.Processor;
import net.pushover.client.*;
import org.apache.commons.math3.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.StringWriter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class AlertService {

	private static final Logger logger = LoggerFactory.getLogger(AlertService.class);

	@Autowired
	private ProfileService profileService;

	@Autowired
	private ProcessorService processorService;

	private PushoverKey defaultPushoverKey;

	public static int MAX_TITLE_LENGTH = 250;
	public static int MAX_MESSAGE_LENGTH = 1024;
	private static final int MAX_DATA_ITEM_LENGTH = 120;
	private static final String FIELD_DELIMITER = "\n--\n";

	private Map<Processor, FrequencyLimiter> processorFrequenceyLimiter = new ConcurrentHashMap<>();
	private FrequencyLimiter globalFrequencyLimiter;

	@PostConstruct
	public void initializeFrequencyLimiters() {
		processorFrequenceyLimiter = new ConcurrentHashMap<>();
		int maxAlertsPerMinute = processorService.getProfileService().profile().getGlobalMaxAlertsPerMinute();
		globalFrequencyLimiter = new FrequencyLimiter(TimeUnit.MINUTES.toMillis(1), maxAlertsPerMinute);
	}

	@PostConstruct
	public void initializePushoverKey() {
		PushoverSettings settings = profileService.profile().getPushoverSettings();
		String applicationName = profileService.profile().getDefaultPushoverApplication();
		String groupName = profileService.profile().getDefaultPushoverGroup();
		defaultPushoverKey = settings.makeKey(applicationName, groupName);
	}

	public synchronized void alert(Alert alert) {
		alert.setTimestamp(System.currentTimeMillis());
		PushoverKey pushoverKey = alert.getPushoverKey();
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
		logger.info("Alert: " + title);
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
		if (checkFrequencyLimits(alert)) {
			return;
		}
		message = truncate(message, MAX_MESSAGE_LENGTH);
		send(pushoverKey, title, message, priority, retry, expire);
	}

	private boolean checkFrequencyLimits(Alert alert) {
		PushoverKey pushoverKey = alert.getPushoverKey();
		Processor emitter = alert.getEmitter();
		if (emitter != null) {
			FrequencyLimiter limiter = processorFrequenceyLimiter.get(alert.getEmitter());
			if (limiter == null) {
				int maxAlertsPerMinute = processorService.getProfileService().profile().getAlertGeneratorMaxAlertsPerMinute();
				limiter = new FrequencyLimiter(TimeUnit.MINUTES.toMillis(1), maxAlertsPerMinute);
				processorFrequenceyLimiter.put(emitter, limiter);
			}
			int overflow = limiter.ping();
			if (overflow > 0) {
				if (overflow == 1) {
					int max = limiter.getMaxPerPeriod();
					String title = "Limit of " + max + " alerts per alerter reached on " + processorService.getHostName();
					String message = "Muting for a moment: " + emitter.getLocation().descriptor();
					send(pushoverKey, title, message, MessagePriority.NORMAL, null, null);
					logger.warn("Alert not sent: max alerter alerts per minutes reached for " + emitter.getLocation().descriptor());
				}
				return true;
			}
		}
		int overflow = globalFrequencyLimiter.ping();
		if (overflow > 0) {
			if (overflow == 1) {
				int max = globalFrequencyLimiter.getMaxPerPeriod();
				String title = "Global limit of " + max + " alerts reached on " + processorService.getHostName();
				String message ="Muting for a moment: " + processorService.getHostName();
				send(pushoverKey, title, message, MessagePriority.NORMAL, null, null);
				logger.warn("Alert not sent: Global max alerts per minutes reached for " + emitter.getLocation().descriptor());
			}
			return true;
		}
		return false;
	}

	private String truncate(String str, int length) {
		if (str.length() <= length) {
			return str;
		} else {
			return str.substring(0, Math.max(0, length - 3)) + "...";
		}
	}

	private void send(PushoverKey pushoverKey, String title, String message, MessagePriority priority, Integer retry, Integer expire) {
		if (profileService.profile().isInteractive()) {
			logger.warn("Alert not sent: test mode");
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

	PushoverKey getDefaultPushoverKey() {
		return defaultPushoverKey;
	}

}

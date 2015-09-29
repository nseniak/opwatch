package com.untrackr.alerter.model.common;

import com.untrackr.alerter.common.ApplicationUtil;

import java.util.concurrent.TimeUnit;

public class AlerterProfile {

	private long fileWatchingCheckDelay;
	private long executorTerminationTimeout;
	private PushoverKey pushoverKey;
	private long tailedFileWatchingCheckDelay;
	private long tailPollInterval;
	private Integer defaultEmergencyRetry;
	private Integer defaultEmergencyExpire;
	private long defaultScheduledProducerPeriod;
	private boolean testMode;
	private int maxAlertsPerMinute;

	public AlerterProfile() {
		this.fileWatchingCheckDelay = TimeUnit.SECONDS.toMillis(1);
		this.executorTerminationTimeout = TimeUnit.SECONDS.toMillis(30);
		// Key for Trackbuster app/Trackbuster dev group
		this.pushoverKey = new PushoverKey("aU5Pkdp2uDPxDM2yQw9UCVg9Nz9287", "g1viaT81UbvSkZ9N2eVGSXMU56TLic");
		this.tailedFileWatchingCheckDelay = TimeUnit.SECONDS.toMillis(1);
		this.tailPollInterval = TimeUnit.MILLISECONDS.toMillis(500);
		this.defaultEmergencyRetry = 60;
		this.defaultEmergencyExpire = 3600;
		this.defaultScheduledProducerPeriod = TimeUnit.MILLISECONDS.toMillis(1000);
		this.testMode = ApplicationUtil.property("alerter.test", false);
		this.maxAlertsPerMinute = 10;
	}

	public long getFileWatchingCheckDelay() {
		return fileWatchingCheckDelay;
	}

	public void setFileWatchingCheckDelay(long fileWatchingCheckDelay) {
		this.fileWatchingCheckDelay = fileWatchingCheckDelay;
	}

	public long getExecutorTerminationTimeout() {
		return executorTerminationTimeout;
	}

	public void setExecutorTerminationTimeout(long executorTerminationTimeout) {
		this.executorTerminationTimeout = executorTerminationTimeout;
	}

	public PushoverKey getPushoverKey() {
		return pushoverKey;
	}

	public void setPushoverKey(PushoverKey pushoverKey) {
		this.pushoverKey = pushoverKey;
	}

	public long getTailedFileWatchingCheckDelay() {
		return tailedFileWatchingCheckDelay;
	}

	public void setTailedFileWatchingCheckDelay(long tailedFileWatchingCheckDelay) {
		this.tailedFileWatchingCheckDelay = tailedFileWatchingCheckDelay;
	}

	public long getTailPollInterval() {
		return tailPollInterval;
	}

	public void setTailPollInterval(long tailPollInterval) {
		this.tailPollInterval = tailPollInterval;
	}

	public Integer getDefaultEmergencyRetry() {
		return defaultEmergencyRetry;
	}

	public void setDefaultEmergencyRetry(Integer defaultEmergencyRetry) {
		this.defaultEmergencyRetry = defaultEmergencyRetry;
	}

	public Integer getDefaultEmergencyExpire() {
		return defaultEmergencyExpire;
	}

	public void setDefaultEmergencyExpire(Integer defaultEmergencyExpire) {
		this.defaultEmergencyExpire = defaultEmergencyExpire;
	}

	public long getDefaultScheduledProducerPeriod() {
		return defaultScheduledProducerPeriod;
	}

	public void setDefaultScheduledProducerPeriod(long defaultScheduledProducerPeriod) {
		this.defaultScheduledProducerPeriod = defaultScheduledProducerPeriod;
	}

	public boolean isTestMode() {
		return testMode;
	}

	public void setTestMode(boolean testMode) {
		this.testMode = testMode;
	}

	public int getMaxAlertsPerMinute() {
		return maxAlertsPerMinute;
	}

	public void setMaxAlertsPerMinute(int maxAlertsPerMinute) {
		this.maxAlertsPerMinute = maxAlertsPerMinute;
	}

}

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
	private boolean interactive;
	private boolean trace;
	private int maxAlertsPerMinute;
	private long defaultHttpConnectTimeout;
	private long defaultHttpReadTimeout;
	private int lineBufferSize;
	private int inputQueueSize;
	private long processorInputQueueTimeout;
	private long minimumOutputDelay;

	public AlerterProfile() {
		this.fileWatchingCheckDelay = TimeUnit.SECONDS.toMillis(1);
		this.executorTerminationTimeout = TimeUnit.SECONDS.toMillis(30);
		// Key for Trackbuster app/Trackbuster dev group
		this.pushoverKey = new PushoverKey("aU5Pkdp2uDPxDM2yQw9UCVg9Nz9287", "g1viaT81UbvSkZ9N2eVGSXMU56TLic");
		this.tailedFileWatchingCheckDelay = TimeUnit.SECONDS.toMillis(1);
		this.tailPollInterval = TimeUnit.MILLISECONDS.toMillis(500);
		this.defaultEmergencyRetry = 60;
		this.defaultEmergencyExpire = 3600;
		this.defaultScheduledProducerPeriod = TimeUnit.SECONDS.toMillis(10);
		this.interactive = ApplicationUtil.property("alerter.interactive", false);
		this.trace = ApplicationUtil.property("alerter.trace", false);
		this.maxAlertsPerMinute = 10;
		this.defaultHttpConnectTimeout = TimeUnit.SECONDS.toMillis(5);
		this.defaultHttpReadTimeout = TimeUnit.SECONDS.toMillis(10);
		this.lineBufferSize = 8192 * 100;
		this.inputQueueSize = 100;
		this.processorInputQueueTimeout = TimeUnit.SECONDS.toMillis(60);
		this.minimumOutputDelay = TimeUnit.MILLISECONDS.toMillis(50);
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

	public boolean isInteractive() {
		return interactive;
	}

	public void setInteractive(boolean interactive) {
		this.interactive = interactive;
	}

	public boolean isTrace() {
		return trace;
	}

	public void setTrace(boolean trace) {
		this.trace = trace;
	}

	public int getMaxAlertsPerMinute() {
		return maxAlertsPerMinute;
	}

	public void setMaxAlertsPerMinute(int maxAlertsPerMinute) {
		this.maxAlertsPerMinute = maxAlertsPerMinute;
	}

	public long getDefaultHttpConnectTimeout() {
		return defaultHttpConnectTimeout;
	}

	public void setDefaultHttpConnectTimeout(long defaultHttpConnectTimeout) {
		this.defaultHttpConnectTimeout = defaultHttpConnectTimeout;
	}

	public long getDefaultHttpReadTimeout() {
		return defaultHttpReadTimeout;
	}

	public void setDefaultHttpReadTimeout(long defaultHttpReadTimeout) {
		this.defaultHttpReadTimeout = defaultHttpReadTimeout;
	}

	public int getLineBufferSize() {
		return lineBufferSize;
	}

	public void setLineBufferSize(int lineBufferSize) {
		this.lineBufferSize = lineBufferSize;
	}

	public int getInputQueueSize() {
		return inputQueueSize;
	}

	public void setInputQueueSize(int inputQueueSize) {
		this.inputQueueSize = inputQueueSize;
	}

	public long getProcessorInputQueueTimeout() {
		return processorInputQueueTimeout;
	}

	public void setProcessorInputQueueTimeout(long processorInputQueueTimeout) {
		this.processorInputQueueTimeout = processorInputQueueTimeout;
	}

	public long getMinimumOutputDelay() {
		return minimumOutputDelay;
	}

	public void setMinimumOutputDelay(long minimumOutputDelay) {
		this.minimumOutputDelay = minimumOutputDelay;
	}

}


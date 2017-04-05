package com.untrackr.alerter.service;

import com.untrackr.alerter.common.ApplicationUtil;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class AlerterConfig {

	private long fileWatchingCheckDelay;
	private long executorTerminationTimeout;
	private long tailedFileWatchingCheckDelay;
	private long tailPollInterval;
	private boolean channelDebug;
	private boolean trace;
	private int lineBufferSize;
	private int inputQueueSize;
	private long processorInputQueueTimeout;
	private long minimumOutputDelay;
	private String defaultPostHostname;
	private int defaultPostPort;
	private long cronScriptOutputCheckDelay;
	private long cronCommandExitTimeout;
	private long commandStartTimeout;
	private long commandStartSleepTime;

	public AlerterConfig() throws IOException {
		this.fileWatchingCheckDelay = TimeUnit.SECONDS.toMillis(1);
		this.executorTerminationTimeout = TimeUnit.SECONDS.toMillis(30);
		this.tailedFileWatchingCheckDelay = TimeUnit.SECONDS.toMillis(1);
		this.tailPollInterval = TimeUnit.MILLISECONDS.toMillis(100);
		this.channelDebug = ApplicationUtil.property("alerter.channel.debug", false);
		this.trace = ApplicationUtil.property("alerter.trace", false);
		this.lineBufferSize = ApplicationUtil.property("alerter.line.buffer.size", 8192 * 100);
		this.inputQueueSize = ApplicationUtil.property("alerter.input.queue.size", 100);
		this.processorInputQueueTimeout = ApplicationUtil.property("alerter.input.queue.timeout", TimeUnit.SECONDS.toMillis(60));
		this.minimumOutputDelay = ApplicationUtil.property("alerter.output.min.delay", TimeUnit.MILLISECONDS.toMillis(0));
		this.defaultPostHostname = ApplicationUtil.property("alerter.post.hostname", "localhost");
		this.defaultPostPort = ApplicationUtil.property("alerter.post.port", 28018);
		this.cronScriptOutputCheckDelay = TimeUnit.SECONDS.toMillis(100);
		this.cronCommandExitTimeout = TimeUnit.MINUTES.toMillis(3);
		this.commandStartTimeout = TimeUnit.MINUTES.toMillis(1);
		this.commandStartSleepTime = TimeUnit.MILLISECONDS.toMillis(200);
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

	public static String defaultScheduledProducerPeriod() {
		return "1s";
	}

	public boolean isChannelDebug() {
		return channelDebug;
	}

	public void setChannelDebug(boolean channelDebug) {
		this.channelDebug = channelDebug;
	}

	public boolean isTrace() {
		return trace;
	}

	public void setTrace(boolean trace) {
		this.trace = trace;
	}

	public static String defaultHttpConnectTimeout() {
		return "5s";
	}

	public static String defaultHttpReadTimeout() {
		return "10s";
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

	public String getDefaultPostHostname() {
		return defaultPostHostname;
	}

	public void setDefaultPostHostname(String defaultPostHostname) {
		this.defaultPostHostname = defaultPostHostname;
	}

	public int getDefaultPostPort() {
		return defaultPostPort;
	}

	public void setDefaultPostPort(int defaultPostPort) {
		this.defaultPostPort = defaultPostPort;
	}

	public long getCronScriptOutputCheckDelay() {
		return cronScriptOutputCheckDelay;
	}

	public void setCronScriptOutputCheckDelay(long cronScriptOutputCheckDelay) {
		this.cronScriptOutputCheckDelay = cronScriptOutputCheckDelay;
	}

	public long getCronCommandExitTimeout() {
		return cronCommandExitTimeout;
	}

	public void setCronCommandExitTimeout(long cronCommandExitTimeout) {
		this.cronCommandExitTimeout = cronCommandExitTimeout;
	}

	public long getCommandStartTimeout() {
		return commandStartTimeout;
	}

	public void setCommandStartTimeout(long commandStartTimeout) {
		this.commandStartTimeout = commandStartTimeout;
	}

	public long getCommandStartSleepTime() {
		return commandStartSleepTime;
	}

	public void setCommandStartSleepTime(long commandStartSleepTime) {
		this.commandStartSleepTime = commandStartSleepTime;
	}

}

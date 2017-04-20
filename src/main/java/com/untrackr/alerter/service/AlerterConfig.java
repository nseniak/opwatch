package com.untrackr.alerter.service;

import com.untrackr.alerter.CommandLineOptions;
import com.untrackr.alerter.channel.common.ChannelConfig;
import com.untrackr.alerter.common.ApplicationUtil;
import com.untrackr.alerter.processor.common.RuntimeError;
import com.untrackr.alerter.processor.common.ValueLocation;

import java.util.concurrent.TimeUnit;

import static com.untrackr.alerter.AlerterApplication.DEFAULT_HTTP_PORT;

public class AlerterConfig {

	private ProcessorService processorService;
	private ChannelConfig channels;
	private String hostName;
	private String initFile;
	private boolean noInit;
	private boolean noHttp;
	private long fileWatchingCheckDelay;
	private long executorTerminationTimeout;
	private long tailedFileWatchingCheckDelay;
	private long tailPollInterval;
	private boolean traceChannels;
	private boolean traceProcessors;
	private int lineBufferSize;
	private int inputQueueSize;
	private long processorInputQueueTimeout;
	private String defaultPostHostname;
	private int defaultPostPort;
	private long cronScriptOutputCheckDelay;
	private long cronCommandExitTimeout;
	private long commandStartTimeout;
	private long commandStartSleepTime;

	public AlerterConfig(ProcessorService processorService, CommandLineOptions options) {
		this.processorService = processorService;
		this.hostName = options.getHostname();
		this.initFile = options.getInitScript();
		this.noInit = options.isNoInit();
		this.noHttp = options.isNoServer();
		this.traceChannels = options.isTraceChannels();
		this.fileWatchingCheckDelay = TimeUnit.SECONDS.toMillis(1);
		this.executorTerminationTimeout = TimeUnit.SECONDS.toMillis(30);
		this.tailedFileWatchingCheckDelay = TimeUnit.SECONDS.toMillis(1);
		this.tailPollInterval = TimeUnit.MILLISECONDS.toMillis(100);
		this.traceProcessors = ApplicationUtil.property("alerter.trace", false);
		this.lineBufferSize = ApplicationUtil.property("alerter.line.buffer.size", 8192 * 100);
		this.inputQueueSize = ApplicationUtil.property("alerter.input.queue.size", 100);
		this.processorInputQueueTimeout = ApplicationUtil.property("alerter.input.queue.timeout", TimeUnit.SECONDS.toMillis(60));
		this.defaultPostHostname = ApplicationUtil.property("alerter.post.hostname", "localhost");
		this.defaultPostPort = ApplicationUtil.property("alerter.post.port", DEFAULT_HTTP_PORT);
		this.cronScriptOutputCheckDelay = TimeUnit.SECONDS.toMillis(100);
		this.cronCommandExitTimeout = TimeUnit.MINUTES.toMillis(3);
		this.commandStartTimeout = TimeUnit.MINUTES.toMillis(1);
		this.commandStartSleepTime = TimeUnit.MILLISECONDS.toMillis(200);
	}

	public void channels(Object scriptObject) {
		ChannelConfig config = (ChannelConfig) processorService.getScriptService().convertScriptValue(ValueLocation.makeToplevel(), ChannelConfig.class, scriptObject, RuntimeError::new);
		processorService.getMessagingService().initializeChannels(config);
		channels = config;
	}

	public ChannelConfig channels() {
		return channels;
	}

	public String hostName() {
		return hostName;
	}

	public void hostName(String hostName) {
		this.hostName = hostName;
	}

	public String initFile() {
		return initFile;
	}

	public boolean noInit() {
		return noInit;
	}

	public boolean noHttp() {
		return noHttp;
	}

	public long fileWatchingCheckDelay() {
		return fileWatchingCheckDelay;
	}

	public void fileWatchingCheckDelay(long fileWatchingCheckDelay) {
		this.fileWatchingCheckDelay = fileWatchingCheckDelay;
	}

	public long executorTerminationTimeout() {
		return executorTerminationTimeout;
	}

	public void executorTerminationTimeout(long executorTerminationTimeout) {
		this.executorTerminationTimeout = executorTerminationTimeout;
	}

	public long tailedFileWatchingCheckDelay() {
		return tailedFileWatchingCheckDelay;
	}

	public void tailedFileWatchingCheckDelay(long tailedFileWatchingCheckDelay) {
		this.tailedFileWatchingCheckDelay = tailedFileWatchingCheckDelay;
	}

	public long tailPollInterval() {
		return tailPollInterval;
	}

	public void tailPollInterval(long tailPollInterval) {
		this.tailPollInterval = tailPollInterval;
	}

	public static String defaultScheduledProducerPeriod() {
		return "1s";
	}

	public boolean channelDebug() {
		return traceChannels;
	}

	public void channelDebug(boolean channelDebug) {
		this.traceChannels = channelDebug;
	}

	public boolean trace() {
		return traceProcessors;
	}

	public void trace(boolean trace) {
		this.traceProcessors = trace;
	}

	public static String defaultHttpConnectTimeout() {
		return "5s";
	}

	public static String defaultHttpReadTimeout() {
		return "10s";
	}

	public int lineBufferSize() {
		return lineBufferSize;
	}

	public void lineBufferSize(int lineBufferSize) {
		this.lineBufferSize = lineBufferSize;
	}

	public int inputQueueSize() {
		return inputQueueSize;
	}

	public void inputQueueSize(int inputQueueSize) {
		this.inputQueueSize = inputQueueSize;
	}

	public long processorInputQueueTimeout() {
		return processorInputQueueTimeout;
	}

	public void processorInputQueueTimeout(long processorInputQueueTimeout) {
		this.processorInputQueueTimeout = processorInputQueueTimeout;
	}

	public String defaultPostHostname() {
		return defaultPostHostname;
	}

	public void defaultPostHostname(String defaultPostHostname) {
		this.defaultPostHostname = defaultPostHostname;
	}

	public int defaultPostPort() {
		return defaultPostPort;
	}

	public void defaultPostPort(int defaultPostPort) {
		this.defaultPostPort = defaultPostPort;
	}

	public long cronScriptOutputCheckDelay() {
		return cronScriptOutputCheckDelay;
	}

	public void cronScriptOutputCheckDelay(long cronScriptOutputCheckDelay) {
		this.cronScriptOutputCheckDelay = cronScriptOutputCheckDelay;
	}

	public long cronCommandExitTimeout() {
		return cronCommandExitTimeout;
	}

	public void cronCommandExitTimeout(long cronCommandExitTimeout) {
		this.cronCommandExitTimeout = cronCommandExitTimeout;
	}

	public long commandStartTimeout() {
		return commandStartTimeout;
	}

	public void commandStartTimeout(long commandStartTimeout) {
		this.commandStartTimeout = commandStartTimeout;
	}

	public long commandStartSleepTime() {
		return commandStartSleepTime;
	}

	public void commandStartSleepTime(long commandStartSleepTime) {
		this.commandStartSleepTime = commandStartSleepTime;
	}

}

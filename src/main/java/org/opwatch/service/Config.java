/*
 * Copyright (c) 2016-2017 by OMC Inc and other Opwatch contributors
 *
 * Licensed under the Apache License, Version 2.0  (the "License").  You may obtain
 * a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied.  See the License for
 * the specific language governing permissions and limitations under the License.
 */

package org.opwatch.service;

import org.opwatch.CommandLineOptions;
import org.opwatch.channel.common.ChannelConfig;
import org.opwatch.common.ApplicationUtil;
import org.opwatch.processor.common.RuntimeError;
import org.opwatch.processor.common.ValueLocation;
import org.opwatch.processor.config.Duration;

import java.util.concurrent.TimeUnit;

import static org.opwatch.Application.DEFAULT_HTTP_PORT;

public class Config {

	private ProcessorService processorService;
	private ChannelConfig channels;
	private String hostName;
	private String configFile;
	private boolean noConfig;
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
	private long shScriptOutputCheckDelay;
	private long shCommandExitTimeout;
	private long commandStartTimeout;
	private long commandStartSleepTime;
	private boolean tailedFileChangedAlert;
	private long tailedFileMissingAlertDelay;

	public Config(ProcessorService processorService, CommandLineOptions options) {
		this.processorService = processorService;
		this.hostName = options.getHostname();
		this.configFile = options.getConfigScript();
		this.noConfig = options.isNoConfig();
		this.noHttp = options.isNoServer();
		this.traceChannels = options.isTraceChannels();
		this.fileWatchingCheckDelay = TimeUnit.SECONDS.toMillis(1);
		this.executorTerminationTimeout = TimeUnit.SECONDS.toMillis(30);
		this.tailedFileWatchingCheckDelay = TimeUnit.SECONDS.toMillis(1);
		this.tailPollInterval = TimeUnit.MILLISECONDS.toMillis(100);
		this.traceProcessors = ApplicationUtil.property("opwatch.trace", false);
		this.lineBufferSize = ApplicationUtil.property("opwatch.line.buffer.size", 8192 * 100);
		this.inputQueueSize = ApplicationUtil.property("opwatch.input.queue.size", 100);
		this.processorInputQueueTimeout = ApplicationUtil.property("opwatch.input.queue.timeout", TimeUnit.SECONDS.toMillis(60));
		this.defaultPostHostname = ApplicationUtil.property("opwatch.post.hostname", "localhost");
		this.defaultPostPort = ApplicationUtil.property("opwatch.post.port", DEFAULT_HTTP_PORT);
		this.shScriptOutputCheckDelay = TimeUnit.SECONDS.toMillis(100);
		this.shCommandExitTimeout = -1;
		this.commandStartTimeout = TimeUnit.MINUTES.toMillis(1);
		this.commandStartSleepTime = TimeUnit.MILLISECONDS.toMillis(200);
		this.tailedFileChangedAlert = ApplicationUtil.property("opwatch.tail.changeAlerts", false);
		this.tailedFileMissingAlertDelay = ApplicationUtil.property("opwatch.tail.missingFileAlertDelay", TimeUnit.SECONDS.toMillis(15));
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
		return configFile;
	}

	public boolean noInit() {
		return noConfig;
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

	public static Duration defaultScheduledProducerPeriod() {
		return Duration.makeText("10s");
	}

	public static Duration defaultScheduledProducerDelay() {
		return Duration.makeText("0s");
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

	public static Duration defaultHttpConnectTimeout() {
		return Duration.makeText("5s");
	}

	public static Duration defaultHttpReadTimeout() {
		return Duration.makeText("10s");
	}

	public static int defaultMaxRedirects() {
		return 50;
	}

	public static boolean defaultFollowRedirects() {
		return true;
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

	public long shScriptOutputCheckDelay() {
		return shScriptOutputCheckDelay;
	}

	public void shScriptOutputCheckDelay(long shScriptOutputCheckDelay) {
		this.shScriptOutputCheckDelay = shScriptOutputCheckDelay;
	}

	public long shCommandExitTimeout() {
		return shCommandExitTimeout;
	}

	public void shCommandExitTimeout(long shCommandExitTimeout) {
		this.shCommandExitTimeout = shCommandExitTimeout;
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

	public static String defaultLogMaxSize() {
		return "1mb";
	}

	public static String defaultLogTotalMaxSize() {
		return "10mb";
	}

	public static int defaultLogMaxHistory() {
		return 10;
	}

	public static String defaultLogCompression() {
		return "gz";
	}

    public boolean tailedFileFileChangeAlerts() {
        return tailedFileChangedAlert;
    }

    public long tailedFileMissingAlertDelay() {
        return tailedFileMissingAlertDelay;
    }
}

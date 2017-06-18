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

package org.opwatch.channel.common.throttled;

import org.opwatch.channel.common.Channel;
import org.opwatch.channel.common.MessageService;
import org.opwatch.channel.common.ServiceConfiguration;
import org.opwatch.common.ThreadUtil;
import org.opwatch.service.ProcessorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class ThrottledMessageService<S extends ServiceConfiguration> implements MessageService<S>, Runnable {

	private static final Logger logger = LoggerFactory.getLogger(ThrottledMessageService.class);

	@Autowired
	private ProcessorService processorService;

	private ScheduledThreadPoolExecutor scheduledExecutor = new ScheduledThreadPoolExecutor(1, ThreadUtil.threadFactory("RateLimitedChannelServiceTask"));
	private List<ThrottledChannel<S>> channels = new ArrayList<>();

	@PostConstruct
	public void schedule() {
		scheduledExecutor.scheduleAtFixedRate(this, 0, TimeUnit.SECONDS.toMillis(1), TimeUnit.MILLISECONDS);
	}

	@PreDestroy
	public void stop() {
		ThreadUtil.safeExecutorShutdownNow(scheduledExecutor, "RateLimitedChannelServiceExecutor", processorService.config().executorTerminationTimeout());
	}

	@Override
	public List<Channel> channels() {
		return new ArrayList<>(channels);
	}

	@Override
	public void createChannels(S config) {
		channels = doCreateChannels(config);
	}

	@Override
	public void run() {
		try {
			for (ThrottledChannel<S> channel : channels) {
				channel.handlePendingMessages();
			}
		} catch (Throwable t) {
			logger.error("Error running rate limited message service: " + serviceName(), t);
		}
	}

	protected abstract List<ThrottledChannel<S>> doCreateChannels(S config);

	public long timestampSeconds() {
		return TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
	}

	public ProcessorService getProcessorService() {
		return processorService;
	}

}

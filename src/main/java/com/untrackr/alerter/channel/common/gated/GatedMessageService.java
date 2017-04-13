package com.untrackr.alerter.channel.common.gated;

import com.untrackr.alerter.channel.common.Channel;
import com.untrackr.alerter.channel.common.MessageService;
import com.untrackr.alerter.channel.common.ServiceConfiguration;
import com.untrackr.alerter.common.ThreadUtil;
import com.untrackr.alerter.service.ProcessorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class GatedMessageService<S extends ServiceConfiguration> implements MessageService<S>, Runnable {

	private static final Logger logger = LoggerFactory.getLogger(GatedMessageService.class);

	@Autowired
	private ProcessorService processorService;

	private ScheduledThreadPoolExecutor scheduledExecutor = new ScheduledThreadPoolExecutor(1, ThreadUtil.threadFactory("RateLimitedChannelServiceTask"));
	private List<GatedChannel<S>> channels = new ArrayList<>();

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
			for (GatedChannel<S> channel : channels) {
				channel.handlePendingMessages();
			}
		} catch (Throwable t) {
			logger.error("Error running rate limited message service: " + serviceName(), t);
		}
	}

	protected abstract List<GatedChannel<S>> doCreateChannels(S config);

	public long timestampSeconds() {
		return TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
	}

	public ProcessorService getProcessorService() {
		return processorService;
	}

}

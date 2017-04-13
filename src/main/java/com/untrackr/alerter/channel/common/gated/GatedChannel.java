package com.untrackr.alerter.channel.common.gated;

import com.untrackr.alerter.channel.common.Channel;
import com.untrackr.alerter.channel.common.ServiceConfiguration;
import com.untrackr.alerter.processor.common.Message;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class GatedChannel<S extends ServiceConfiguration> implements Channel {

	private GatedMessageService<S> gatedMessageService;
	private ConcurrentLinkedQueue<Message> waitingQueue;
	private Rate currentLimit;
	private Date mutedOn;

	protected GatedChannel(GatedMessageService<S> gatedMessageService) {
		this.gatedMessageService = gatedMessageService;
		this.currentLimit = null;
		this.waitingQueue = new ConcurrentLinkedQueue<>();
	}

	@Override
	public String serviceName() {
		return gatedMessageService.serviceName();
	}

	@Override
	public String name() {
		return null;
	}

	@Override
	public void publish(Message message) {
		waitingQueue.add(message);
		handlePendingMessages();
	}

	public void handlePendingMessages() {
		RateLimiter rateLimiter = rateLimiter();
		synchronized (rateLimiter.root()) {
			while (!waitingQueue.isEmpty()) {
				long now = gatedMessageService.timestampSeconds();
				Rate limitReached = rateLimiter.exceededRate(now);
				if (limitReached == null) {
					if (currentLimit != null) {
						List<Message> messages = new ArrayList<>(waitingQueue);
						publishAggregate(messages, mutedOn);
						waitingQueue.clear();
						currentLimit = null;
						mutedOn = null;
					} else {
						publishOne(waitingQueue.remove());
					}
					rateLimiter.consume(now, 1);
				} else {
					if (currentLimit == null) {
						currentLimit = limitReached;
						mutedOn = new Date();
						publishLimitReached(limitReached);
					}
					return;
				}
			}
		}
	}

	protected String limitReachedMessage(Rate rateLimit) {
		return "Limit of " + rateLimit.describe("message", "messages") + " reached on "
				+ gatedMessageService.getProcessorService().config().hostName();
	}

	protected abstract RateLimiter rateLimiter();

	protected abstract void publishLimitReached(Rate rateLimit);

	protected abstract void publishOne(Message message);

	protected abstract void publishAggregate(List<Message> messages, Date mutedOn);

}

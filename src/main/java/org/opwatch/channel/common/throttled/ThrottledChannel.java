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

import org.opwatch.channel.common.ChannelImpl;
import org.opwatch.channel.common.ServiceConfiguration;
import org.opwatch.processor.common.Message;
import org.opwatch.service.ProcessorService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class ThrottledChannel<S extends ServiceConfiguration> extends ChannelImpl {

	private ThrottledMessageService<S> throttledMessageService;
	private ConcurrentLinkedQueue<Message> waitingQueue;
	private Rate currentLimit;
	private Date mutedOn;

	protected ThrottledChannel(ProcessorService processorService, ThrottledMessageService<S> throttledMessageService) {
		super(processorService);
		this.throttledMessageService = throttledMessageService;
		this.currentLimit = null;
		this.waitingQueue = new ConcurrentLinkedQueue<>();
	}

	@Override
	public String serviceName() {
		return throttledMessageService.serviceName();
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
				long now = throttledMessageService.timestampSeconds();
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
				+ throttledMessageService.getProcessorService().hostName();
	}

	protected abstract RateLimiter rateLimiter();

	protected abstract void publishLimitReached(Rate rateLimit);

	protected abstract void publishOne(Message message);

	protected abstract void publishAggregate(List<Message> messages, Date mutedOn);

}

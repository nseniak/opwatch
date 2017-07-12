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

package org.opwatch.processor.primitives.producer;

import org.opwatch.processor.common.Processor;
import org.opwatch.processor.common.ProcessorVoidExecutionScope;
import org.opwatch.processor.common.RuntimeError;
import org.opwatch.processor.common.SchedulingInfo;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ScheduledProducerExecutor {

	private ScheduledExecutorService executorService;
	private SchedulingInfo schedulingInfo;
	private ScheduledFuture<?> scheduledFuture;

	public ScheduledProducerExecutor(ScheduledExecutorService executorService, SchedulingInfo schedulingInfo) {
		this.executorService = executorService;
		this.schedulingInfo = schedulingInfo;
	}

	public void schedule(Runnable command) {
		scheduledFuture = executorService.scheduleAtFixedRate(command,
				schedulingInfo.getDelay(),
				schedulingInfo.getPeriod(),
				TimeUnit.MILLISECONDS);
	}

	public void stop(Processor<?> processor) {
		boolean stopped = scheduledFuture.cancel(true);
		if (!stopped) {
			throw new RuntimeError("couldn't stop scheduled producer", new ProcessorVoidExecutionScope(processor));
		}
	}

	public boolean running() {
		return !scheduledFuture.isCancelled();
	}

}

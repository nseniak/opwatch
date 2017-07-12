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

package org.opwatch.processor.primitives.producer.sh;

import org.opwatch.common.ThreadUtil;
import org.opwatch.processor.common.ActiveProcessor;
import org.opwatch.processor.common.CommandInfo;
import org.opwatch.processor.common.ProcessorVoidExecutionScope;
import org.opwatch.processor.common.SchedulingInfo;
import org.opwatch.processor.payload.Payload;
import org.opwatch.processor.primitives.producer.CommandRunner;
import org.opwatch.processor.primitives.producer.ScheduledProducerExecutor;
import org.opwatch.service.ProcessorService;

import java.util.concurrent.ScheduledThreadPoolExecutor;

public class Sh extends ActiveProcessor<ShConfig> {

	private SchedulingInfo schedulingInfo;
	private CommandInfo commandInfo;
	private CommandRunner commandRunner;
	private ScheduledThreadPoolExecutor scheduledExecutorService;
	private ScheduledProducerExecutor scheduledExecutor;

	public Sh(ProcessorService processorService,
						ShConfig configuration,
						String name,
						SchedulingInfo schedulingInfo,
						CommandInfo commandInfo) {
		super(processorService, configuration, name);
		this.schedulingInfo = schedulingInfo;
		this.commandInfo = commandInfo;
	}

	@Override
	public void start() {
		scheduledExecutorService =  new ScheduledThreadPoolExecutor(1, ThreadUtil.threadFactory("ShProducerTask"));
		scheduledExecutor = new ScheduledProducerExecutor(scheduledExecutorService, schedulingInfo);
		scheduledExecutor.schedule(() -> processorService.withExceptionHandling("error running scheduled processor",
				() -> new ProcessorVoidExecutionScope(this),
				this::produce));
	}

	@Override
	public void stop() {
		if (scheduledExecutor != null) {
			scheduledExecutor.stop(this);
		}
		if (commandRunner != null) {
			commandRunner.stopProcess();
		}
		if (scheduledExecutorService != null) {
			ThreadUtil.safeExecutorShutdownNow(scheduledExecutorService, "ShProducerExecutor", processorService.config().executorTerminationTimeout());
		}
	}

	@Override
	public void consume(Payload payload) {
		commandRunner.consume(this, payload);
	}

	protected void produce() {
		commandRunner = new CommandRunner(processorService, commandInfo);
		commandRunner.startProcess(this);
		long exitTimeout = processorService.config().shCommandExitTimeout();
		commandRunner.produce(this, exitTimeout);
	}

}

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

package org.opwatch.processor.common;

import org.opwatch.processor.config.ScheduledProcessorConfig;
import org.opwatch.processor.primitives.producer.ScheduledProducerExecutor;
import org.opwatch.service.ProcessorService;

public abstract class ScheduledProcessor<D extends ScheduledProcessorConfig> extends ActiveProcessor<D> {

	private SchedulingInfo schedulingInfo;
	private ScheduledProducerExecutor scheduledExecutor;

	public ScheduledProcessor(ProcessorService processorService, D configuration, String name, SchedulingInfo schedulingInfo) {
		super(processorService, configuration, name);
		this.schedulingInfo = schedulingInfo;
	}

	@Override
	public void start() {
		this.scheduledExecutor = new ScheduledProducerExecutor(processorService.getProducerScheduledExecutor(), schedulingInfo);
		scheduledExecutor.schedule(() -> processorService.withExceptionHandling("error running scheduled processor",
				() -> new ProcessorVoidExecutionScope(this),
				this::produce));
	}

	@Override
	public void stop() {
		scheduledExecutor.stop(this);
	}

	protected abstract void produce();

	protected boolean running() {
		return scheduledExecutor.running();
	}

}

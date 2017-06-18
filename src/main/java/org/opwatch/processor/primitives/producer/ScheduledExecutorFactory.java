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

import org.opwatch.processor.common.ActiveProcessor;
import org.opwatch.processor.common.ActiveProcessorFactory;
import org.opwatch.processor.common.FactoryExecutionScope;
import org.opwatch.processor.common.RuntimeError;
import org.opwatch.processor.config.ScheduledProcessorConfig;
import org.opwatch.service.ProcessorService;

public abstract class ScheduledExecutorFactory<D extends ScheduledProcessorConfig, P extends ActiveProcessor> extends ActiveProcessorFactory<D, P> {

	public ScheduledExecutorFactory(ProcessorService processorService) {
		super(processorService);
	}

	protected ScheduledExecutor makeScheduledExecutor(ScheduledProcessorConfig descriptor, boolean hasInitialDelay) {
		long period = checkPropertyValue("period", descriptor.getPeriod()).value(this);
		long delay = checkPropertyValue("delay", descriptor.getDelay()).value(this);
		if (period <= 0) {
			throw new RuntimeError("duration must be strictly positive: " + descriptor.getPeriod(),
					new FactoryExecutionScope(this));
		}
		if (delay < 0) {
			throw new RuntimeError("delay must be positive: " + descriptor.getDelay(),
					new FactoryExecutionScope(this));
		}
		return new ScheduledExecutor(processorService, delay, period);
	}

}

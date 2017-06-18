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
import org.opwatch.processor.payload.Payload;
import org.opwatch.processor.payload.SeriesObject;
import org.opwatch.processor.primitives.producer.ScheduledExecutor;
import org.opwatch.service.ProcessorService;

import java.util.concurrent.LinkedBlockingQueue;

public abstract class TrailCollecter<D extends ScheduledProcessorConfig> extends ScheduledProcessor<D> {

	private long duration;
	protected LinkedBlockingQueue<SeriesObject> queue;
	private long startupTimestamp;

	public TrailCollecter(ProcessorService processorService, D configuration, String name, ScheduledExecutor scheduledExecutor, long duration) {
		super(processorService, configuration, name, scheduledExecutor);
		this.duration = duration;
		this.queue = new LinkedBlockingQueue<>();
	}

	@Override
	public void start() {
		startupTimestamp = System.currentTimeMillis();
		super.start();
	}

	@Override
	public void consume(Payload payload) {
		long timestamp = System.currentTimeMillis();
		Object result = collectedObject(payload);
		queue.add(new SeriesObject(result, timestamp));
	}

	@Override
	protected void produce() {
		long timestamp = System.currentTimeMillis();
		if ((timestamp - startupTimestamp) < duration) {
			return;
		}
		queue.removeIf(to -> (timestamp - to.getTimestamp()) > duration);
		outputProduced(producedObject());
	}

	protected abstract Object collectedObject(Payload payload);

	protected abstract Object producedObject();

}

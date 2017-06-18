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

import org.opwatch.processor.config.ActiveProcessorConfig;
import org.opwatch.processor.payload.Payload;
import org.opwatch.service.ProcessorService;

import java.util.concurrent.Future;

public abstract class ThreadedConsumer<D extends ActiveProcessorConfig> extends ActiveProcessor<D> {

	private Future<?> consumerThreadFuture;
	private ConsumerThreadRunner consumerThreadRunner;

	public ThreadedConsumer(ProcessorService processorService, D configuration, String name) {
		super(processorService, configuration, name);
	}

	@Override
	public void start() {
		consumerThreadRunner = new ConsumerThreadRunner(processorService, this);
		consumerThreadFuture = processorService.getConsumerExecutor().submit(consumerThreadRunner);
	}

	@Override
	public void stop() {
		if (!consumerThreadFuture.isDone()) {
			boolean stopped = consumerThreadFuture.cancel(true);
			if (!stopped) {
				throw new RuntimeError("cannot stop consumer thread", new ProcessorVoidExecutionScope(this));
			}
		}
	}

	@Override
	public void consume(Payload payload) {
		consumerThreadRunner.consume(payload);
	}

	public abstract void consumeInOwnThread(Payload payload);

}

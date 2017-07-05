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

import org.opwatch.processor.payload.Payload;
import org.opwatch.service.ProcessorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ConsumerThreadRunner implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(ConsumerThreadRunner.class);

	protected ProcessorService processorService;
	protected ThreadedConsumer<?> processor;
	protected ArrayBlockingQueue<Payload> inputQueue;

	public ConsumerThreadRunner(ProcessorService processorService, ThreadedConsumer<?> processor) {
		this.processorService = processorService;
		this.processor = processor;
		int inputQueueSize = processorService.config().inputQueueSize();
		this.inputQueue = new ArrayBlockingQueue<>(inputQueueSize);
	}

	public void consume(Payload payload) {
		long timeout = processorService.config().processorInputQueueTimeout();
		try {
			while (!inputQueue.offer(payload, timeout, TimeUnit.MILLISECONDS)) {
				processorService.signalSystemException(new RuntimeError("pipe full", new ProcessorPayloadExecutionScope(processor, payload)));
			}
		} catch (InterruptedException e) {
			// Shutting down.
			throw new ApplicationInterruptedException(ApplicationInterruptedException.INTERRUPTION);
		}
	}

	@Override
	public void run() {
		while (true) {
			processorService.withExceptionHandling("error processing input",
					() -> new ProcessorVoidExecutionScope(processor),
					() -> {
						Payload payload = inputQueue.take();
						processorService.withExceptionHandling("error processing input",
								() -> new ProcessorPayloadExecutionScope(processor, payload),
								() -> processor.consumeInOwnThread(payload));
					});
		}
	}

}

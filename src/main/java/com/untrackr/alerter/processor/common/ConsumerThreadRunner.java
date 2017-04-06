package com.untrackr.alerter.processor.common;

import com.untrackr.alerter.processor.payload.Payload;
import com.untrackr.alerter.service.ProcessorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ConsumerThreadRunner implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(ConsumerThreadRunner.class);

	protected ProcessorService processorService;
	protected ActiveProcessor<?> processor;
	protected ArrayBlockingQueue<Payload<?>> inputQueue;

	public ConsumerThreadRunner(ProcessorService processorService, ActiveProcessor<?> processor) {
		this.processorService = processorService;
		this.processor = processor;
		int inputQueueSize = processorService.config().inputQueueSize();
		this.inputQueue = new ArrayBlockingQueue<>(inputQueueSize);
	}

	public void consume(Payload<?> payload) {
		long timeout = processorService.config().processorInputQueueTimeout();
		try {
			while (!inputQueue.offer(payload, timeout, TimeUnit.MILLISECONDS)) {
				processorService.signalSystemException(new RuntimeError("pipe full", new ProcessorPayloadExecutionContext(processor, payload)));
			}
		} catch (InterruptedException e) {
			// Shutting down.
			throw new ApplicationInterruptedException(ApplicationInterruptedException.INTERRUPTION);
		}
	}

	@Override
	public void run() {
		while (true) {
			processorService.withExceptionHandling("error reading input",
					new ProcessorVoidExecutionContext(processor),
					() -> {
						Payload<?> payload = inputQueue.take();
						processorService.withExceptionHandling("error processing input",
								new ProcessorPayloadExecutionContext(processor, payload),
								() -> processor.consumeInOwnThread(payload));
					});
		}
	}

}

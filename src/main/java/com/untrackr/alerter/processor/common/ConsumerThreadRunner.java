package com.untrackr.alerter.processor.common;

import com.untrackr.alerter.model.common.Alert;
import com.untrackr.alerter.service.ProcessorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ConsumerThreadRunner implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(ConsumerThreadRunner.class);

	protected ProcessorService processorService;
	protected Processor processor;
	protected ArrayBlockingQueue<Payload> inputQueue;

	public ConsumerThreadRunner(ProcessorService processorService, Processor processor) {
		this.processorService = processorService;
		this.processor = processor;
		int inputQueueSize = processorService.getProfileService().profile().getInputQueueSize();
		this.inputQueue = new ArrayBlockingQueue<>(inputQueueSize);
	}

	public void consume(Payload payload) {
		try {
			long timeout = processorService.getProfileService().profile().getProcessorInputQueueTimeout();
			while (!inputQueue.offer(payload, timeout, TimeUnit.MILLISECONDS)) {
				processorService.infrastructureAlert(Alert.Priority.high, "Processor queue is full", processor.pathDescriptor());
			}
		} catch (InterruptedException e) {
			// Nothing to do: the application is exiting.
		}
	}

	@Override
	public void run() {
		while (true) {
			try {
				Payload payload = inputQueue.take();
				processorService.withErrorHandling(processor, payload, () -> processor.consume(payload));
			} catch (InterruptedException e) {
				// Nothing to do: the application is exiting.
				return;
			}
		}
	}

}

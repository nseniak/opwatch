package com.untrackr.alerter.processor.primitives.producer;

import com.untrackr.alerter.processor.common.ActiveProcessor;
import com.untrackr.alerter.processor.common.RuntimeError;
import com.untrackr.alerter.processor.common.ProcessorVoidExecutionScope;
import com.untrackr.alerter.processor.config.ActiveProcessorConfig;
import com.untrackr.alerter.processor.payload.Payload;
import com.untrackr.alerter.service.ProcessorService;

public abstract class Producer<D extends ActiveProcessorConfig> extends ActiveProcessor<D> {

	public Producer(ProcessorService processorService, D descriptor, String name) {
		super(processorService, descriptor, name);
	}

	@Override
	public void consumeInOwnThread(Payload<?> payload) {
		throw new RuntimeError("producer should not receive input", new ProcessorVoidExecutionScope(this));
	}

}

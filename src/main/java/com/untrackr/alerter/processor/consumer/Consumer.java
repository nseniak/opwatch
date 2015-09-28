package com.untrackr.alerter.processor.consumer;

import com.untrackr.alerter.model.descriptor.IncludePath;
import com.untrackr.alerter.processor.common.ActiveProcessor;
import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.common.ProcessorSignature;
import com.untrackr.alerter.service.ProcessorService;

public abstract class Consumer extends ActiveProcessor {

	public Consumer(ProcessorService processorService, IncludePath path) {
		super(processorService, path);
		this.signature = ProcessorSignature.makeConsumer();
	}

}

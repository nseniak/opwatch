package com.untrackr.alerter.processor.filter;

import com.untrackr.alerter.processor.common.IncludePath;
import com.untrackr.alerter.processor.common.ActiveProcessor;
import com.untrackr.alerter.processor.common.ProcessorSignature;
import com.untrackr.alerter.service.ProcessorService;

public abstract class Filter extends ActiveProcessor {

	public Filter(ProcessorService processorService, IncludePath path) {
		super(processorService, path);
		this.signature = ProcessorSignature.makeFilter();
	}

	@Override
	public void doStart() {
		createConsumerThread();
	}

	@Override
	public void doStop() {
		stopConsumerThread();
	}

}

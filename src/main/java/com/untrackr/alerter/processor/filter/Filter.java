package com.untrackr.alerter.processor.filter;

import com.untrackr.alerter.model.descriptor.IncludePath;
import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.common.ProcessorSignature;
import com.untrackr.alerter.service.ProcessorService;

public abstract class Filter extends Processor {

	public Filter(ProcessorService processorService, IncludePath path) {
		super(processorService, path);
		this.signature = ProcessorSignature.makeFilter();
	}

}

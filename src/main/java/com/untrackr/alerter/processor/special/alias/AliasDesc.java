package com.untrackr.alerter.processor.special.alias;

import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.common.ProcessorDesc;

public class AliasDesc extends ProcessorDesc {

	private Processor processor;
	private Object descriptor;

	public Processor getProcessor() {
		return processor;
	}

	public void setProcessor(Processor processor) {
		this.processor = processor;
	}

	public Object getDescriptor() {
		return descriptor;
	}

	public void setDescriptor(Object descriptor) {
		this.descriptor = descriptor;
	}

}

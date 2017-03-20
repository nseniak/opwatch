package com.untrackr.alerter.processor.primitives.special.alias;

import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.descriptor.ProcessorDescriptor;

public class AliasDescriptor extends ProcessorDescriptor {

	private String name;
	private Processor processor;
	private Object descriptor;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

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

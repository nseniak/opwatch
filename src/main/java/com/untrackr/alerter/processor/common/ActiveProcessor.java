package com.untrackr.alerter.processor.common;

import com.untrackr.alerter.model.descriptor.IncludePath;
import com.untrackr.alerter.service.ProcessorService;

public abstract class ActiveProcessor extends Processor {

	private String name;

	public ActiveProcessor(ProcessorService processorService, IncludePath path) {
		super(processorService, path);
	}

	@Override
	public String descriptor() {
		String id = identifier();
		if (id == null) {
			return super.descriptor();
		} else {
			return type() + "{'" + id + "'}";
		}
	}

	public  String identifier() {
		// Default
		return name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}

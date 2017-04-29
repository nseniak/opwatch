package org.opwatch.processor.primitives.control.alias;

import org.opwatch.processor.common.Processor;
import org.opwatch.processor.config.ProcessorConfig;

public class AliasConfig extends ProcessorConfig {

	private String name;
	private Processor processor;
	private Object configuration;

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

	public Object getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Object configuration) {
		this.configuration = configuration;
	}

}

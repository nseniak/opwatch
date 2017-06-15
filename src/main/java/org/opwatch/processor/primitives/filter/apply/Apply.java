package org.opwatch.processor.primitives.filter.apply;

import jdk.nashorn.internal.runtime.ScriptRuntime;
import org.opwatch.processor.config.JavascriptFilter;
import org.opwatch.processor.payload.Payload;
import org.opwatch.processor.primitives.filter.Filter;
import org.opwatch.service.ProcessorService;

public class Apply extends Filter<ApplyConfig> {

	private JavascriptFilter output;

	public Apply(ProcessorService processorService, ApplyConfig configuration, String name, JavascriptFilter output) {
		super(processorService, configuration, name);
		this.output = output;
	}

	@Override
	public void consume(Payload payload) {
		Object result = output.call(payload, this);
		if (result != ScriptRuntime.UNDEFINED) {
			outputTransformed(result, payload);
		}
	}

}

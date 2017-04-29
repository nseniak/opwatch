package org.opwatch.processor.primitives.filter.apply;

import org.opwatch.processor.config.JavascriptFilter;
import org.opwatch.processor.payload.Payload;
import org.opwatch.processor.primitives.filter.Filter;
import org.opwatch.service.ProcessorService;

public class Apply extends Filter<ApplyConfig> {

	private JavascriptFilter lambda;

	public Apply(ProcessorService processorService, ApplyConfig configuration, String name, JavascriptFilter lambda) {
		super(processorService, configuration, name);
		this.lambda = lambda;
	}

	@Override
	public void consume(Payload<?> payload) {
		Object result = lambda.call(payload, this);
		outputTransformed(result, payload);
	}

}

package com.untrackr.alerter.processor.primitives.filter.apply;

import com.untrackr.alerter.processor.config.JavascriptFilter;
import com.untrackr.alerter.processor.payload.Payload;
import com.untrackr.alerter.processor.primitives.filter.Filter;
import com.untrackr.alerter.service.ProcessorService;

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

package com.untrackr.alerter.processor.primitives.filter.test;

import com.untrackr.alerter.processor.config.JavascriptPredicate;
import com.untrackr.alerter.processor.payload.Payload;
import com.untrackr.alerter.processor.primitives.filter.ConditionalFilter;
import com.untrackr.alerter.service.ProcessorService;

public class Test extends ConditionalFilter<TestConfig> {

	private JavascriptPredicate lambda;

	public Test(ProcessorService processorService, TestConfig descriptor, String name, JavascriptPredicate lambda) {
		super(processorService, descriptor, name);
		this.lambda = lambda;
	}

	@Override
	public boolean predicateValue(Payload input) {
		return lambda.call(input, this);
	}

}

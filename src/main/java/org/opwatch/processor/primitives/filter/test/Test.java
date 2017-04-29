package org.opwatch.processor.primitives.filter.test;

import org.opwatch.processor.config.JavascriptPredicate;
import org.opwatch.processor.payload.Payload;
import org.opwatch.processor.primitives.filter.ConditionalFilter;
import org.opwatch.service.ProcessorService;

public class Test extends ConditionalFilter<TestConfig> {

	private JavascriptPredicate lambda;

	public Test(ProcessorService processorService, TestConfig configuration, String name, JavascriptPredicate lambda) {
		super(processorService, configuration, name);
		this.lambda = lambda;
	}

	@Override
	public boolean predicateValue(Payload input) {
		return lambda.call(input, this);
	}

}

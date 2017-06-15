package org.opwatch.processor.primitives.filter.test;

import org.opwatch.processor.config.JavascriptPredicate;
import org.opwatch.processor.payload.Payload;
import org.opwatch.processor.primitives.filter.ConditionalFilter;
import org.opwatch.service.ProcessorService;

public class Test extends ConditionalFilter<TestConfig> {

	private JavascriptPredicate predicate;

	public Test(ProcessorService processorService, TestConfig configuration, String name, JavascriptPredicate predicate) {
		super(processorService, configuration, name);
		this.predicate = predicate;
	}

	@Override
	public boolean predicateValue(Payload input) {
		return predicate.call(input, this);
	}

}

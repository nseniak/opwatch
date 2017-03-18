package com.untrackr.alerter.processor.primitives.transformer.jsgrep;

import com.untrackr.alerter.processor.descriptor.JavascriptPredicate;
import com.untrackr.alerter.processor.payload.Payload;
import com.untrackr.alerter.processor.primitives.transformer.ConditionalTransformer;
import com.untrackr.alerter.service.ProcessorService;

public class JSGrep extends ConditionalTransformer<JSGrepDescriptor> {

	private JavascriptPredicate predicate;

	public JSGrep(ProcessorService processorService, JSGrepDescriptor descriptor, String name, JavascriptPredicate predicate) {
		super(processorService, descriptor, name);
		this.predicate = predicate;
	}

	@Override
	public boolean predicateValue(Payload input) {
		return predicate.call(input, this);
	}

}

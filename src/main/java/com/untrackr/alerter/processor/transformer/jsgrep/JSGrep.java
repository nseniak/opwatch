package com.untrackr.alerter.processor.transformer.jsgrep;

import com.untrackr.alerter.processor.common.JavascriptPredicate;
import com.untrackr.alerter.processor.common.Payload;
import com.untrackr.alerter.processor.transformer.ConditionalTransformer;
import com.untrackr.alerter.service.ProcessorService;

public class JSGrep extends ConditionalTransformer {

	private JavascriptPredicate predicate;

	public JSGrep(ProcessorService processorService, JSGrepDesc descriptor, String name, JavascriptPredicate predicate) {
		super(processorService, descriptor, name);
		this.predicate = predicate;
	}

	@Override
	public boolean predicateValue(Payload input) {
		return predicate.call(input, this);
	}

}

package com.untrackr.alerter.processor.filter.jsgrep;

import com.untrackr.alerter.processor.common.JavascriptPredicate;
import com.untrackr.alerter.processor.common.Payload;
import com.untrackr.alerter.processor.common.ScriptStack;
import com.untrackr.alerter.processor.filter.ConditionalFilter;
import com.untrackr.alerter.service.ProcessorService;

public class JSGrep extends ConditionalFilter {

	private JavascriptPredicate predicate;

	public JSGrep(ProcessorService processorService, ScriptStack stack, JavascriptPredicate predicate) {
		super(processorService, stack);
		this.predicate = predicate;
	}

	@Override
	public boolean predicateValue(Payload input) {
		return predicate.call(input, this);
	}

	@Override
	public String identifier() {
		return predicate.toString();
	}

}

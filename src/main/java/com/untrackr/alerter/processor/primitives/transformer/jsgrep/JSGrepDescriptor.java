package com.untrackr.alerter.processor.primitives.transformer.jsgrep;

import com.untrackr.alerter.processor.descriptor.ActiveProcessorDescriptor;
import com.untrackr.alerter.processor.descriptor.JavascriptPredicate;

public class JSGrepDescriptor extends ActiveProcessorDescriptor {

	private JavascriptPredicate predicate;

	public JavascriptPredicate getPredicate() {
		return predicate;
	}

	public void setPredicate(JavascriptPredicate predicate) {
		this.predicate = predicate;
	}

}

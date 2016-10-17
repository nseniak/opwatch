package com.untrackr.alerter.processor.transformer.jsgrep;

import com.untrackr.alerter.processor.common.ActiveProcessorDesc;
import com.untrackr.alerter.processor.common.JavascriptPredicate;

public class JSGrepDesc extends ActiveProcessorDesc {

	private JavascriptPredicate predicate;

	public JavascriptPredicate getPredicate() {
		return predicate;
	}

	public void setPredicate(JavascriptPredicate predicate) {
		this.predicate = predicate;
	}

}

package com.untrackr.alerter.processor.transformer.js;

import com.untrackr.alerter.processor.common.JavascriptTransformer;
import com.untrackr.alerter.processor.common.Payload;
import com.untrackr.alerter.processor.common.ScriptStack;
import com.untrackr.alerter.processor.transformer.Transformer;
import com.untrackr.alerter.service.ProcessorService;

public class JS extends Transformer {

	private JavascriptTransformer transformer;

	public JS(ProcessorService processorService, ScriptStack stack, JavascriptTransformer transformer) {
		super(processorService, stack);
		this.transformer = transformer;
	}

	@Override
	public void consume(Payload payload) {
		Object result = transformer.call(payload, this);
		if (result != null) {
			outputTransformed(result, payload);
		}
	}

	@Override
	public String identifier() {
		return transformer.toString();
	}

}

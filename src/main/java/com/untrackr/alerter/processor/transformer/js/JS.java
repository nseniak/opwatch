package com.untrackr.alerter.processor.transformer.js;

import com.untrackr.alerter.processor.common.JavascriptTransformer;
import com.untrackr.alerter.processor.common.Payload;
import com.untrackr.alerter.processor.transformer.Transformer;
import com.untrackr.alerter.service.ProcessorService;

public class JS extends Transformer {

	private JavascriptTransformer transformer;

	public JS(ProcessorService processorService, String name, JavascriptTransformer transformer) {
		super(processorService, name);
		this.transformer = transformer;
	}

	@Override
	public void consume(Payload payload) {
		Object result = transformer.call(payload, this);
		if (result != null) {
			outputTransformed(result, payload);
		}
	}

}

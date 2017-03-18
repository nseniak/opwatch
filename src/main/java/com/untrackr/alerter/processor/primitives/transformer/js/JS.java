package com.untrackr.alerter.processor.primitives.transformer.js;

import com.untrackr.alerter.processor.descriptor.JavascriptTransformer;
import com.untrackr.alerter.processor.payload.Payload;
import com.untrackr.alerter.processor.primitives.transformer.Transformer;
import com.untrackr.alerter.service.ProcessorService;

public class JS extends Transformer<JSDescriptor> {

	private JavascriptTransformer transformer;

	public JS(ProcessorService processorService, JSDescriptor descriptor, String name, JavascriptTransformer transformer) {
		super(processorService, descriptor, name);
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

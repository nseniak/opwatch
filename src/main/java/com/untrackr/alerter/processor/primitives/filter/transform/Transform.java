package com.untrackr.alerter.processor.primitives.filter.transform;

import com.untrackr.alerter.processor.descriptor.JavascriptFilter;
import com.untrackr.alerter.processor.payload.Payload;
import com.untrackr.alerter.processor.primitives.filter.Filter;
import com.untrackr.alerter.service.ProcessorService;

public class Transform extends Filter<TransformDescriptor> {

	private JavascriptFilter transformer;

	public Transform(ProcessorService processorService, TransformDescriptor descriptor, String name, JavascriptFilter transformer) {
		super(processorService, descriptor, name);
		this.transformer = transformer;
	}

	@Override
	public void consumeInOwnThread(Payload<?> payload) {
		Object result = transformer.call(payload, this);
		if (result != null) {
			outputTransformed(result, payload);
		}
	}

}

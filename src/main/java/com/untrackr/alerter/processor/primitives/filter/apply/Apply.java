package com.untrackr.alerter.processor.primitives.filter.apply;

import com.untrackr.alerter.processor.descriptor.JavascriptFilter;
import com.untrackr.alerter.processor.payload.Payload;
import com.untrackr.alerter.processor.primitives.filter.Filter;
import com.untrackr.alerter.service.ProcessorService;

public class Apply extends Filter<ApplyDescriptor> {

	private JavascriptFilter transformer;

	public Apply(ProcessorService processorService, ApplyDescriptor descriptor, String name, JavascriptFilter transformer) {
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

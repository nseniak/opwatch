package com.untrackr.alerter.processor.primitives.filter.json;

import com.untrackr.alerter.processor.common.RuntimeError;
import com.untrackr.alerter.processor.common.ProcessorPayloadExecutionScope;
import com.untrackr.alerter.processor.payload.Payload;
import com.untrackr.alerter.processor.primitives.filter.Filter;
import com.untrackr.alerter.service.ProcessorService;

import java.io.IOException;

public class Json extends Filter<JsonConfig> {

	public Json(ProcessorService processorService, JsonConfig descriptor, String name) {
		super(processorService, descriptor, name);
	}

	@Override
	public void consumeInOwnThread(Payload<?> payload) {
		String value = payloadValue(payload, String.class);
		Object result = null;
		try {
			result = processorService.parseJson(value);
		} catch (IOException e) {
			throw new RuntimeError("cannot parse json: " + e.getMessage(),
					new ProcessorPayloadExecutionScope(this, payload),
					e);
		}
		outputTransformed(result, payload);
	}

}

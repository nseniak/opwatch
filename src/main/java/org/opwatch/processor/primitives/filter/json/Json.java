package org.opwatch.processor.primitives.filter.json;

import org.opwatch.processor.common.RuntimeError;
import org.opwatch.processor.common.ProcessorPayloadExecutionScope;
import org.opwatch.processor.payload.Payload;
import org.opwatch.processor.primitives.filter.Filter;
import org.opwatch.service.ProcessorService;

import java.io.IOException;

public class Json extends Filter<JsonConfig> {

	public Json(ProcessorService processorService, JsonConfig configuration, String name) {
		super(processorService, configuration, name);
	}

	@Override
	public void consume(Payload<?> payload) {
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

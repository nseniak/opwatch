package com.untrackr.alerter.processor.transformer.json;

import com.untrackr.alerter.processor.common.AlerterException;
import com.untrackr.alerter.processor.common.ExceptionContext;
import com.untrackr.alerter.processor.common.Payload;
import com.untrackr.alerter.processor.transformer.Transformer;
import com.untrackr.alerter.service.ProcessorService;

public class Json extends Transformer<JsonDesc> {

	public Json(ProcessorService processorService, JsonDesc descriptor, String name) {
		super(processorService, descriptor, name);
	}

	@Override
	public void consume(Payload payload) {
		try {
			String value = payloadValue(payload, String.class);
			Object result = processorService.parseJson(value);
			outputTransformed(result, payload);
		} catch (Throwable t) {
			// This code is running in a file tailing thread; throwing an exception would do no good as the exception
			// would be caught by this thread. We display the error.
			processorService.displayAlerterException(new AlerterException("cannot parse json: " + t.getMessage(),
					ExceptionContext.makeProcessorPayload(this, payload)));
			return;
		}
	}

}
